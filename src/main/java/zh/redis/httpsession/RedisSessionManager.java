package zh.redis.httpsession;

import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import zh.redis.RedisClient;
import zh.redis.RedisSimpleTempalte;
import org.apache.commons.lang.StringUtils;

public class RedisSessionManager {
	public static final String SESSION_ID_PREFIX = "RJSID_";
	public static final String SESSION_ID_COOKIE = "RSESSIONID";
	private RedisSimpleTempalte redisClient[];
	private IRedisSessionLoadBalance loadBalance;
	//Session最大更新间隔时间
	private int expirationUpdateInterval;
	//Session过期时间
	private int sessionTimeOut;

	public RedisSessionManager() {
		this.expirationUpdateInterval = 600;
		this.sessionTimeOut = 1800;
	}
	
	public RedisSessionManager(String host,String port) {
		this(host,port,1800,null);
	}
	
	public RedisSessionManager(String host,String port,String loadBalanceClass) {
		this(host,port,1800,loadBalanceClass);
	}
	
	public RedisSessionManager(String host,String port,int sessionTimeOut,String loadBalanceClass) {
		this.expirationUpdateInterval = 300;
		this.sessionTimeOut = sessionTimeOut;
		String[] hosts = host.split(";");
		String[] ports = port.split(";");
		if(hosts.length!=ports.length)
			throw new IllegalStateException("Inconsistent quantity of host and Port");
		this.redisClient = new RedisSimpleTempalte[hosts.length];
		//生成RedisClient对象
		for(int i=0;i<hosts.length;i++){
			String hostStr = hosts[i];
			int portInt = Integer.parseInt(ports[i]);
			redisClient[i] = new RedisSimpleTempalte(new RedisClient(hostStr, portInt));
		}
		//实例化Redis的LoadBlance
		if(loadBalanceClass == null){
			loadBalance = new DefaultRedisSessionLoadBalance();
		}
		else{
			try {
				Class<?> cls = Class.forName(loadBalanceClass);
				try {
					loadBalance = (IRedisSessionLoadBalance) cls.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void setRedisClient(RedisSimpleTempalte[] redisClient) {
		this.redisClient = redisClient;
	}
	
	public RedisSimpleTempalte getRedisClient(String sessionId){
		return this.loadBalance.getRedisClient(sessionId, this.redisClient);
	}

	public void setExpirationUpdateInterval(int expirationUpdateInterval) {
		this.expirationUpdateInterval = expirationUpdateInterval;
	}

	public void setMaxInactiveInterval(int sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}

	/**
	 * 每次请求取得最新Session
	 * @param request
	 * @param response
	 * @param requestEventSubject
	 * @param create
	 * @return
	 */
	public RedisHttpSession createSession(
			SessionHttpServletRequestWrapper request,
			HttpServletResponse response,
			RequestEventSubject requestEventSubject, boolean create) {
		String sessionId = getRequestedSessionId(request);

		RedisHttpSession session = null;
		//首次登录没有SeeionID，并且不创建新Session则不处理
		if ((StringUtils.isEmpty(sessionId)) && (!(create)))
			return null;
		//如果SessionID不为空则从Redis加载Session
		if (StringUtils.isNotEmpty(sessionId))
			session = loadSession(sessionId);
		//如果是首次登录则Session为空,生成空Session
		if ((session == null) && (create))
			session = createEmptySession(request, response);
		//如果Session不为空则，附加各种回调事件
		if (session != null)
            attachEvent(session, request, response, requestEventSubject);
		
		return session;
	}

	/**
	 * 从Request的Cookies中取出SessionId
	 * @param request
	 * @return
	 */
    private String getRequestedSessionId(HttpServletRequestWrapper request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null || cookies.length==0)return null;
        for(Cookie cookie: cookies){
            if(SESSION_ID_COOKIE.equals(cookie.getName()))return cookie.getValue();
        }
        return null;
    }

    /**
     * 保存Session
     * @param session
     */
	private void saveSession(RedisHttpSession session) {
		String sessionid = generatorSessionKey(session.id);
		try {
			//如果Session过期
			if (session.expired){
				//清楚本地缓存数据
//				CacheUtil.removeKey(session.id);
				//清楚Redis中的数据
				this.getRedisClient(session.id).del(sessionid);
			}
			else{
				//在本地缓存中保存Session
//				CacheUtil.put(session.id, session);
				//在远程Redis中保存Session并且重新设置过期时间
				this.getRedisClient(session.id).set(sessionid, SeesionSerializer.serialize(session), this.sessionTimeOut);
			}
		} catch (Exception e) {
			throw new SessionException(e);
		}
	}
	
	/**
	 * 增加Session过期和Request请求结束后的回调事件
	 * @param session
	 * @param request
	 * @param response
	 * @param requestEventSubject
	 */
	private void attachEvent(final RedisHttpSession session, final HttpServletRequestWrapper request, final HttpServletResponse response, RequestEventSubject requestEventSubject) {
        session.setListener(new SessionListener(){
            public void onInvalidated(RedisHttpSession session) {
            	//设置客户端Cookies过期
                saveCookie(session, request, response);
                //保存Redis中的Session信息
                saveSession(session);
            }
        });
        requestEventSubject.attach(new RequestEventObserver() {
            public void completed(HttpServletRequest servletRequest, HttpServletResponse response) {
                int updateInterval = (int) ((System.currentTimeMillis() - session.lastAccessedTime) / 1000);
                	//如果Session是初始化的空Session则需要同步到Redis
                if (session.isNew == false
                	//如果 Session一致 并且在最小间隔同步时间内  则不与Redis同步
                	&& session.isDirty == false 
                	&& updateInterval < expirationUpdateInterval)
                    return;
                //如果 Session过期不与Redis同步
                if (session.expired) return;
                session.lastAccessedTime = System.currentTimeMillis();
                saveSession(session);
            }
        });
    }

	/**
	 * 初始化空Session
	 * @param request
	 * @param response
	 * @return
	 */
	private RedisHttpSession createEmptySession(
			SessionHttpServletRequestWrapper request,
			HttpServletResponse response) {
		RedisHttpSession session = new RedisHttpSession();
		session.id = createSessionId();
		session.creationTime = System.currentTimeMillis();
		session.maxInactiveInterval = this.sessionTimeOut;
		session.isNew = true;
		saveCookie(session, request, response);
		return session;
	}

	private String createSessionId() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}

    private void saveCookie(RedisHttpSession session, HttpServletRequestWrapper request, HttpServletResponse response) {
        if (session.isNew == false && session.expired == false) return;

        Cookie cookie = new Cookie(SESSION_ID_COOKIE, null);
        cookie.setPath(request.getContextPath());
        //如果Session过期则Cookies也过期
        if(session.expired){
            cookie.setMaxAge(0);
        //如果Session是新生成的，则需要在客户端设置SessionID
        }else if (session.isNew){
            cookie.setValue(session.getId());
		}
        response.addCookie(cookie);
    }

    /**
     * 从Redis中重新加载Session
     * @param sessionId
     * @return
     */
	private RedisHttpSession loadSession(String sessionId) {
		RedisHttpSession session;
		try {
			//先成本地缓存中加载
//			Object cacheSession = CacheUtil.getSerialVal(sessionId);
//			if(cacheSession != null)
//				session = (RedisHttpSession)cacheSession;
//			else
				//从远程缓存中加载
				session = SeesionSerializer.deserialize(this.getRedisClient(sessionId).getByte(generatorSessionKey(sessionId)));
			//重新加载到本地缓存的Session需要重新设置同步标志与新建标志
			if (session != null) {
				session.isNew = false;
				session.isDirty = false;
			}
			return session;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String generatorSessionKey(String sessionId) {
		return SESSION_ID_PREFIX.concat(sessionId);
	}
}
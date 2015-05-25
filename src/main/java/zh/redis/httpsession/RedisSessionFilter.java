package zh.redis.httpsession;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedisSessionFilter implements Filter {
	//静态资源不做过滤
	public static final String[] IGNORE_SUFFIX = { ".png", ".jpg", ".jpeg",".gif", ".css", ".js", ".html", ".htm", "swf"};
	private RedisSessionManager sessionManager;

	public void init(FilterConfig filterConfig) throws ServletException {
		String port = filterConfig.getInitParameter("port");
		String host = filterConfig.getInitParameter("host");
		String loadBalanceClass = filterConfig.getInitParameter("loadBalanceClass");
		String sessionTimeOutStr = filterConfig.getInitParameter("sessionTimeOut");
		
		if(sessionTimeOutStr == null)
			sessionTimeOutStr = "1800";
		int sessionTimeOut = Integer.parseInt(sessionTimeOutStr);
//		CacheUtil.initCache(sessionTimeOut);
		this.sessionManager = new RedisSessionManager(host, port,sessionTimeOut,loadBalanceClass);
	}

	public void setSessionManager(RedisSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		if (!(ifFilter(request))) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		RequestEventSubject eventSubject = new RequestEventSubject();
		//生成Request包装器，替换原生的Request中的Session
		SessionHttpServletRequestWrapper requestWrapper = new SessionHttpServletRequestWrapper(
				request, response, this.sessionManager, eventSubject);
		try {
			//向下一处理器传递替换后的Request
			filterChain.doFilter(requestWrapper, servletResponse);
		} finally {
			//保存最新的Session
			eventSubject.completed(request, response);
		}
	}

	/**
	 * 是否过滤请求
	 * @param request
	 * @return
	 */
    private boolean ifFilter(HttpServletRequest request) {
        String uri = request.getRequestURI().toLowerCase();
        for (String suffix : IGNORE_SUFFIX) {
            if (uri.endsWith(suffix)) return false;
        }
        return true;
    }

	public void destroy() {
	}
}
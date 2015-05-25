//package zh.redis.httpsession;
//
//import java.io.Serializable;
//
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//import net.sf.ehcache.Element;
//import net.sf.ehcache.event.RegisteredEventListeners;
//import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
//
///**提供缓存相关处理方法(这里使用的ehcache)*/
//public class CacheUtil {
//	
//	private static final String cacheName = "RedisSessionLoaclCache";	
//	private static int  cacheMaxNum = 10000;
//	
//	/**
//	 * 定义缓存淘汰规则的枚举类型,目前支持策略：
//	 * FIFO(先入先出)  LRU(最少使用)  LFU(最少访问)
//	 * 注意：缓存只会抽取最多30个元素来处理,默认使用LRU
//	 */
//	public enum CacheEviction {
//		/**先入先出淘汰规则*/
//		FIFO,
//		/**最少使用淘汰规则*/
//		LRU,
//		/**最少访问淘汰规则*/
//		LFU
//	}
//
//	/**缓存管理类,JVM全局唯一*/
//	static CacheManager manager = CacheManager.create();
//	
//	public static void initCache(int timeOut){
//		CacheUtil.newCache(cacheName, cacheMaxNum, timeOut*10, timeOut, CacheEviction.LRU);
//	}
//	
//	/**1.1增加新的缓存(如果缓存已存在时,将直接返回,淘汰机制为空时,使用LRU)
//	 * @param name 缓存的名称
//	 * @param maxElements 最大缓存元素数目
//	 * @param toLiveSeconds 允许缓存中元素存在于缓存中的最长时间(单位秒)
//	 * @param toIdleSeconds 允许缓存中元素处于空闲状态的最长时间(单位秒)
//	 * @param type 定义的淘汰机制,可用FIFO(先入先出)、LRU(最少使用)、LFU(最少访问)
//	 */
//	public static void newCache(String name, int maxElements,
//			long toLiveSeconds, long toIdleSeconds,CacheEviction type) {
//		if(manager.cacheExists(name)){
//			return;
//		}
//		if(type==null){
//			type = CacheEviction.LRU;
//		}
//		MemoryStoreEvictionPolicy msep = null;
//		switch (type) {
//			case FIFO:
//				msep = MemoryStoreEvictionPolicy.FIFO;	break;
//			case LRU:
//				msep = MemoryStoreEvictionPolicy.LRU;	break;
//			case LFU:
//				msep = MemoryStoreEvictionPolicy.LFU;	break;
//			default:
//				msep = MemoryStoreEvictionPolicy.LRU;	break;
//		}
//		
//		boolean overflowToDisk = false;////当缓存的元素超过界限不会写到硬盘缓存中
//		String diskStorePath = "java.io.tmpDir";
//		
//		boolean eternal = false;
//		//为true表示对象永不过期时，忽略timeToIdleSeconds和timeToLiveSeconds属性，默认为false
//		
//		boolean diskPersistent = false;
//		//是否缓存虚拟机重启时期的数据
//		
//		long diskExpiryThreadIntervalSeconds = 120;
//		//磁盘失效线程运行时间间隔，默认为120秒
//		
//		RegisteredEventListeners registeredEventListeners = null;
//		
//		Cache newCache = new Cache(name, maxElements, msep,overflowToDisk, 
//				diskStorePath, eternal, toLiveSeconds,toIdleSeconds, diskPersistent,
//				diskExpiryThreadIntervalSeconds, registeredEventListeners);
//		manager.addCache(newCache);
//	}
//	
//	/**增加元素到指定名称的缓存(当key存在时会自动执行更新操作)
//	 * @param key 传入的key值
//	 * @param value 传入的value值
//	 */
//	public static void put(Object key, Object value) {
//		put(cacheName,key,value);
//	}
//	
//	protected static void put(String name, Object key, Object value) {
//		Cache cache = manager.getCache(name);
//		cache.put(new Element(key, value));
//	}
//	/**获取缓存里指定key的序列化value值,如果key不存在时返回null
//	 * @param key 传入的key值
//	 * @return 返回序列化对象
//	 */
//	public static Serializable getSerialVal(Object key) {
//		return getSerialVal(cacheName, key);
//	}
//	
//	protected static Serializable getSerialVal(String name,Object key) {
//		Cache cache = manager.getCache(name);
//		if(!cache.isKeyInCache(key)){
//			return null;
//		}else{
//			return cache.get(key).getValue();
//		}
//	}
//	
//	/**移除缓存里指定key的对象value值,如果key不存在时直接返回
//	 * @param key 传入的key值
//	 */
//	public static void removeKey(Object key) {
//		removeKey(cacheName, key);
//	}
//	
//	protected static void removeKey(String name, Object key) {
//		Cache cache = manager.getCache(name);
//		if(!cache.isKeyInCache(key)) return;
//		cache.remove(key);
//	}
//}

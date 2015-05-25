package zh.redis.httpsession;

import zh.redis.RedisSimpleTempalte;

public interface IRedisSessionLoadBalance {
	/**
	 * Redis Session服务负载均衡接口
	 * @param sessionId 用户的SessionID
	 * @param redisClients redis Session服务器列表
	 * @return redis服务对象
	 */
	public RedisSimpleTempalte getRedisClient(String sessionId,RedisSimpleTempalte redisClients[]);
}

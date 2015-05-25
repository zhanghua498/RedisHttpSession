package zh.redis.httpsession;

import zh.redis.RedisSimpleTempalte;

public class DefaultRedisSessionLoadBalance implements IRedisSessionLoadBalance {

	@Override
	public RedisSimpleTempalte getRedisClient(String sessionId,
			RedisSimpleTempalte[] redisClients) {
		int length = redisClients.length;
		int hash = hash(sessionId) % length;
		return redisClients[hash];
	}

	private int hash(String s) {
		int h = 0;
		int len = s.length();
		if (h == 0 && len > 0) {
			int off = 0;
			char val[] = s.toCharArray();
			for (int i = 0; i < len; i++) {
				h = 31 * h + val[off++];
			}
		}
		return Math.abs(h);
	}
}

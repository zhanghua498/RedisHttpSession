package zh.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public abstract class RedisTemplate<T> {
	
	private RedisClient client;
	
	public RedisTemplate(){
		this.client = getRedisClient();
	}
	
	public void execute(T params){
		Jedis jedis = null;
		try{
			jedis = client.getRedis();
			callback(jedis,params);
		}
		catch(JedisConnectionException e){
			if (jedis != null) 
				client.returnBrokeRedis(jedis);
			jedis = client.getRedis();
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if (jedis != null) {
				client.returnRedis(jedis);
			}
		}
	}

	protected abstract RedisClient getRedisClient();
	
	protected abstract void callback(Jedis jedis,T params);

}

package zh.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {

	private JedisPool pool;
	
	public RedisClient(String host,int port){
		JedisPoolConfig poolconfig = new JedisPoolConfig();
		poolconfig.setMaxTotal(200);
		poolconfig.setMaxIdle(20);
		poolconfig.setMaxWaitMillis(1000);
		poolconfig.setTestOnBorrow(true);
		pool = new JedisPool(poolconfig,host,port,30*1000);
	}
	
    public void destory() {
        pool.destroy();
    }

    public Jedis getRedis() {
    	Jedis jedis = pool.getResource();
    	jedis.select(0);
        return jedis;
    }
    
    public Jedis getRedis(int index) {
    	Jedis jedis = pool.getResource();
    	jedis.select(index);
        return jedis;
    }

    public void returnRedis(Jedis jedis) {
        pool.returnResource(jedis);
    }
    
    public void returnBrokeRedis(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }
}

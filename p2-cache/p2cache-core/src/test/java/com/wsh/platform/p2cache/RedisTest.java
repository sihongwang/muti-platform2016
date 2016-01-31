package com.wsh.platform.p2cache;

import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 测试Redis
 * @author wan
 *
 */
public class RedisTest {
	private String redisServerIp  = "192.168.9.205";
	private int redisServerPort = 6379;
	private String redisServerAuth = "ablejava";
	private String redisKeyPre = "wsh_sample____";
	
	//@Test
	public void test1(){
		Jedis redis = new Jedis (redisServerIp, redisServerPort);//连接redis  
		redis.auth(redisServerAuth);//验证密码,如果需要验证的话
		// STRING 操作
		
		//SET key value将字符串值value关联到key。
		redis.set(redisKeyPre + "name", "wangjun1");
		redis.set(redisKeyPre + "id", "123456");
		redis.set(redisKeyPre + "address", "guangzhou");
		
		//SETEX key seconds value将值value关联到key，并将key的生存时间设为seconds(以秒为单位)。
		redis.setex(redisKeyPre + "foo", 5, "haha");
		
		//MSET key value [key value ...]同时设置一个或多个key-value对。
		redis.mset(redisKeyPre + "haha","111",redisKeyPre + "xixi","222");
		
		//redis.flushAll();清空所有的key
		System.out.println(redis.dbSize());//dbSize是多少个key的个数
		
		//APPEND key value如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值之后。
		redis.append(redisKeyPre + "foo", "00");//如果key已经存在并且是一个字符串，APPEND命令将value追加到key原来的值之后。
		
		//GET key 返回key所关联的字符串值
		redis.get(redisKeyPre + "foo");
		
		//MGET key [key ...] 返回所有(一个或多个)给定key的值
		List list = redis.mget(redisKeyPre + "haha",redisKeyPre + "xixi");
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
	}
	
	/**
	 * #最大分配的对象数
redis.pool.maxActive=1024
#最大能够保持idel状态的对象数
redis.pool.maxIdle=200
#当池内没有返回对象时，最大等待时间
redis.pool.maxWait=1000
#当调用borrow Object方法时，是否进行有效性检查
redis.pool.testOnBorrow=true
#当调用return Object方法时，是否进行有效性检查
redis.pool.testOnReturn=true
#IP
redis.ip=172.0.0.1
#Port
redis.port=6379
	 */
	@Test
	public void test2(){
//		ResourceBundle bundle = ResourceBundle.getBundle("redis");
//	    if (bundle == null) {
//	  throw new IllegalArgumentException(
//	    "[redis.properties] is not found!");
//	    }
//	    JedisPoolConfig config = new JedisPoolConfig();
//	    config.setMaxActive(Integer.valueOf(bundle
//	      .getString("redis.pool.maxActive")));
//	    config.setMaxIdle(Integer.valueOf(bundle
//	      .getString("redis.pool.maxIdle")));
//	    config.setMaxWait(Long.valueOf(bundle.getString("redis.pool.maxWait")));
//	    config.setTestOnBorrow(Boolean.valueOf(bundle
//	      .getString("redis.pool.testOnBorrow")));
//	    config.setTestOnReturn(Boolean.valueOf(bundle
//	      .getString("redis.pool.testOnReturn")));
//	    pool = new JedisPool(config, bundle.getString("redis.ip"),
//	      Integer.valueOf(bundle.getString("redis.port")));
		try{
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(8);
			config.setMaxIdle(8);
			config.setMaxWaitMillis(1000);
			config.setTestOnBorrow(false);
			config.setTestOnReturn(false);
			
			JedisPool pool  = new JedisPool(config, redisServerIp, redisServerPort);
			// 从池中获取一个Jedis对象
			Jedis jedis = pool.getResource();
			jedis.auth(redisServerAuth);
			//SET key value将字符串值value关联到key。
			jedis.set(redisKeyPre + "name", "wangjun1" + Math.random());
			String value = jedis.get(redisKeyPre + "name");
			System.out.println(value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

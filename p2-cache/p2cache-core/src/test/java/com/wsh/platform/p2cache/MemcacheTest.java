package com.wsh.platform.p2cache;

import java.util.Map;

import org.junit.Test;

import com.schooner.MemCached.MemcachedItem;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class MemcacheTest {
	private void initPool(){
		String[] servers = {"redhat5:11211"};
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(servers);//设置服务器
		pool.setFailover(true);//容错
		pool.setInitConn(10);//设置初始连接数
		pool.setMinConn(5);//设置最小连接数
		pool.setMaxConn(25);//设置最大连接数
		pool.setMaintSleep(30);//设置连接池维护线程睡眠时间
		pool.setNagle(false);//设置是否使用Nagle算法
		pool.setSocketTO(3000);//设置socket的读取等待超时时间
		pool.setAliveCheck(true);//设置连接心跳监测开头
		pool.setHashingAlg(SockIOPool.CONSISTENT_HASH);//设置Hash算法
		pool.initialize();
	}
	
	@Test
	public void testMemcache(){
		initPool();
		
		String pre = "_mem_test_";
		MemCachedClient memCachedClient = new MemCachedClient();
		
		//add 添加数据，如果存在同样的key，则添加失败
		for(int i = 0; i< 10; i ++){
			boolean flag = memCachedClient.add(pre + i, i);
			System.out.println("testMemcache::add(\"" + pre + i + "\", " + i + ") = " + flag );
		}
		//set 保存数据，如果存在同样的key，则覆盖；如果不存在key，则添加
		for(int i = 0; i< 10; i ++){
			boolean flag = memCachedClient.replace(pre + i, i);
			System.out.println("testMemcache::set(\"" + pre + i + "\", " + i + ") = " + flag );
		}
		//replace 替换数据，如果存在同样的key，则替换；如果不存在key，则替换失败
		for(int i = 0; i< 10; i ++){
			boolean flag = memCachedClient.set(pre + i, "b" + i + "b" );
			System.out.println("testMemcache::replace(\"" + pre + i + "\", " + i + ") = " + flag );
		}
		//apend 追加数据，如果存在同样的key，则追加成功；如果不存在key，则失败
		for(int i = 0; i< 10; i ++){
			boolean flag = memCachedClient.append(pre + i, "c");
			System.out.println("testMemcache::append(\"" + pre + i + "\", c) = " + flag );
		}
		//prepend 追加数据，如果存在同样的key，则追加成功；如果不存在key，则失败
		for(int i = 0; i< 10; i ++){
			boolean flag = memCachedClient.prepend(pre + i, "a");
			System.out.println("testMemcache::prepend(\"" + pre + i + "\", a) = " + flag );
		}
		//get
		for(int i = 0; i< 10; i ++){
			Object data = memCachedClient.get(pre + i);
			System.out.println("testMemcache::get(\"" + pre + i + "\") = " + data );
		}
		System.out.println();
		System.out.println();
		
		//set 保存数据，如果存在同样的key，则覆盖；如果不存在key，则添加
		for(int i = 0; i< 10; i ++){
			boolean flag = memCachedClient.set(pre + i, i);
			System.out.println("testMemcache::set(\"" + pre + i + "\", " + i + ") = " + flag );
		}
		//incr 增量数据，如果存在同样的key，则增量成功；如果不存在key，则失败
		for(int i = 0; i< 10; i ++){
			long flag = memCachedClient.incr(pre + i, 1);
			System.out.println("testMemcache::incr(\"" + pre + i + "\", " + i + ") = " + flag );
		}
		//get
		for(int i = 0; i< 10; i ++){
			Object data = memCachedClient.get(pre + i);
			System.out.println("testMemcache::get(\"" + pre + i + "\") = " + data );
		}
		System.out.println();
		
		//decr 减量数据，如果存在同样的key，则减量成功；如果不存在key，则失败
		for(int i = 0; i< 10; i ++){
			long flag = memCachedClient.decr(pre + i, 1);
			System.out.println("testMemcache::decr(\"" + pre + i + "\", " + 1 + ") = " + flag );
		}
		//get
		for(int i = 0; i< 10; i ++){
			Object data = memCachedClient.get(pre + i);
			System.out.println("testMemcache::get(\"" + pre + i + "\") = " + data );
		}
		
		//cas 提供对变量的cas操作，它将保证在进行数据更新之前，数据没有被其他人更改
		for(int i = 0; i< 10; i ++){
			String key  = pre + i;
			MemcachedItem item = memCachedClient.gets(key);
			int value = (int)item.getValue() + 1;
			boolean flag = memCachedClient.cas(key, value, item.getCasUnique());
			System.out.println("testMemcache::cas(\"" + key + "\", " + value + ", " + item.getCasUnique() + ") = " + flag );
		}
		
		//getMulti
		String[] keys = new String[10];
		for(int i = 0; i< 10; i ++){
			String key  = pre + i;
			keys[i] = key;
		}
		Map<String, Object> datas = memCachedClient.getMulti(keys);
		for(Map.Entry<String, Object> entry : datas.entrySet()){
			System.out.println("testMemcache::getMulti(\"" + entry.getKey() + "\") = " + entry.getValue() );
		}
	}
}

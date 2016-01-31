package com.wsh.platform.p1zookeeper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class ZookeeperTest {
	/**
	 * 创建zookeeper客户端
	 * @return
	 * @throws IOException
	 */
	private ZooKeeper getZooKeeper() throws IOException{
		System.out.println();
		ZooKeeper zooKeeper = new ZooKeeper("zookeeper1.ablejava.com:2181", 1000, new Watcher() {
		      @Override
		      public void process(WatchedEvent event) {
		    	  System.out.println("EventType:" + event.getType().name());
		      }
		 });
		 return zooKeeper;
	}
	
	/**
	 * 获取"/" node下的所有子node
	 * @param zooKeeper
	 * @param path
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void printZookeeperNodes(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException{
		System.out.println();
		List<String> znodes = zooKeeper.getChildren(path, true);
		for (String zpath : znodes) {
		     System.out.println("printZookeeperNodes==" + zpath);
		}
	}
	
	/**
	 * 获取"/" node下的childCount的子node
	 * @param zooKeeper
	 * @param path
	 * @param childCount
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void printZookeeperChildNodes(ZooKeeper zooKeeper, String path, int childCount) throws KeeperException, InterruptedException{
		
		List<String> znodes = zooKeeper.getChildren(path, true);
		int i = 0;
		for (String zpath : znodes) {
			String childPath = path + "/" + zpath;
			System.out.println("printZookeeperNodes==" + childPath);
		     if(i < childCount){
		    	 printZookeeperChildNodes(zooKeeper, childPath, childCount);
		     }
		     i++;
		}
		System.out.println();
	}
	/**
	 * 创建开放权限的持久化node
	 * @param zooKeeper
	 * @param path
	 * @param nodeName
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private String createZnode(ZooKeeper zooKeeper,String path, String nodeName) throws KeeperException, InterruptedException{
		System.out.println();
		String rs = zooKeeper.create(path + nodeName, nodeName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("createZnode==" + rs);
		return rs;
	}
	
	/**同步获取"/test" node的数据
	 * 异步获取"/test" node的数据
	 * @param zooKeeper
	 * @param path
	 * @param withSysDatas 同步
	 * @param withAsyDatas 异步
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private Map<String, String> getZnodeDatas(ZooKeeper zooKeeper, String path, boolean withSysDatas, boolean withAsyDatas) throws KeeperException, InterruptedException{
		System.out.println();
		Map<String, String> map = new HashMap<String, String>();
		Stat stat = new Stat();
		if(withSysDatas){
			byte[] data = zooKeeper.getData(path, true, stat);
			if(data != null){
				String dataValue = new String(data);
				String statStr = stat.toString();
				System.out.println("getZnodeDatas-sys-value==" + dataValue);
				System.out.println("getZnodeDatas-sys-stat==" + statStr);
				map.put("getZnodeDatas-sys-value", dataValue);
				map.put("getZnodeDatas-sys-stat", statStr);
			}
		}
		
		//异步获取"/test" node的数据
		if(withAsyDatas){
		  zooKeeper.getData(path, true, new AsyncCallback.DataCallback() {
		      @Override
		      public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
		    	  String pre = "getZnodeDatas-asy==";
		    	  System.out.println(pre + rc);
				  System.out.println(pre + path);
				  System.out.println(pre + ctx);
				  System.out.printf(pre + new String(data));
				  System.out.println(pre + stat.toString());
		      }
		  }, "Object ctx ..(提供的外部对象)");
		}
		return map;
	}
	
	@Test
	public void testZookeeper(){
		try{
			//创建zookeeper客户端
			  ZooKeeper zooKeeper = getZooKeeper();
			  
			  //获取"/" node下的所有子node
			  //printZookeeperNodes(zooKeeper, "/");
			  //printZookeeperNodes(zooKeeper, "/dubbo/com.able.livecourse.rmi.service.LiveCourseRmiService/providers");
			  //printZookeeperChildNodes(zooKeeper, "/dubbo", 1);
			  
			  //创建开放权限的持久化node "/test"
			  //String rs = createZnode(zooKeeper, "/", "test");

			  //同步获取"/test" node的数据
			  //getZnodeDatas(zooKeeper, "/test", true, true);
			  //getZnodeDatas(zooKeeper, "/dubbo/com.able.livecourse.rmi.service.LiveCourseRmiService", true, true);
			  
			  //TimeUnit.SECONDS.sleep(10);
			  zooKeeper.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	private void addSubscribe(ZkClient zkc, String path){
		zkc.subscribeChildChanges(path, new IZkChildListener(){
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				String pre = "childChanges-handleChildChange-";
				System.out.println(pre + "parentPath==" + parentPath);
				for(String c : currentChilds){
					System.out.println(pre + "currentChilds==" + c);
				}
			}
		});
		
		zkc.subscribeDataChanges(path, new IZkDataListener(){

			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				System.out.println("dataChanges-handleDataChange-dataPath==" + dataPath);
				System.out.println("dataChanges-handleDataChange-data==" + data);
			}

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("dataChanges-handleDataDeleted-dataPath==" + dataPath);
			}
		});
		zkc.subscribeStateChanges(new IZkStateListener(){

			@Override
			public void handleNewSession() throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void handleSessionEstablishmentError(Throwable arg0) throws Exception {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				System.out.println("stateChanges-handleStateChanged-state==" + state);
			}
			
		});
	}
	@Test
	public void testZKClient(){
//		try{
//			//Server + Client
//		    ZkServer zkServer = new ZkServer("D:/data", "D:/log", new IDefaultNameSpace() {
//		      @Override
//		      public void createDefaultNameSpace(ZkClient zkClient) {
//		        zkClient.create("/default", "defalut-name-space", CreateMode.PERSISTENT);
//		      }
//		    });
//		    zkServer.start();
//		    ZkClient zkClient = zkServer.getZkClient();
//		    boolean exists = zkClient.exists("/default");
//		    if (exists) {
//		      System.out.println("default name space init create succeed.");
//		    } else {
//		      System.out.println("default name space init create failed.");
//		    }
//		    System.in.read();
//		    zkClient.close();
//		    zkServer.shutdown();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		try{
			//Only use client
		    ZkClient zkc = new ZkClient("zookeeper1.ablejava.com:2181");
		    String PATH = "/able";
		    String znode = "/test" + Math.random();
		    String pathZnode = PATH + znode;
		    //创建节点
		    if(!zkc.exists(PATH))
		    	zkc.create(PATH, PATH, CreateMode.PERSISTENT);
		    if(!zkc.exists(pathZnode))
		    	zkc.create(pathZnode, pathZnode, CreateMode.PERSISTENT);
		    
		    addSubscribe(zkc, pathZnode);
		    
		    //获取子节点
		    List<String> childrens = zkc.getChildren(PATH);
		    for (String child : childrens) {
		      System.out.println("childdrens==" + PATH + "/" + child);
		    }
		    //获取子节点个数
		    int childCount = zkc.countChildren(PATH);
		    System.out.println("childCount==" + childCount);
		    //判断节点是否存在
		    boolean b = zkc.exists(pathZnode);
		    System.out.println("childexists("+pathZnode+")==" + b);
		    //写入数据
		    zkc.writeData(pathZnode, "hello world");
		    //读取节点数据
		    Object znodeData = zkc.readData(pathZnode);
		    System.out.println("readData("+pathZnode+")==" + znodeData);
		    //删除节点
		    //zkc.delete(pathZnode);
		    zkc.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	    /**
	    try{
	    	//创建zookeeper服务并启动
//		    ZkServer zkServer = new ZkServer(home + "/zookeeper/data", home + "/zookeeper/log", 2181);
//		    zkServer.start();
		  //方式一
			ZkClient zkClient = new ZkClient("zookeeper1.ablejava.com:2181", 1000);
		  //方式二
		  //ZkClient zkClient = zkServer.getZkClient();
		  String path = "/dubbo";
		  //数据监听
		  final IZkDataListener dataListener = new IZkDataListener() {
		      @Override
		      public void handleDataChange(String dataPath, byte[] data) {
		    	  System.out.println(dataPath + " data change");
		      }
		      @Override
		      public void handleDataDeleted(String dataPath) throws Exception {
		      }
			@Override
			public void handleDataChange(String arg0, Object arg1) throws Exception {
				// TODO Auto-generated method stub
				
			}
		  };
		  //结点（node）监听
		  final IZkChildListener childListener = new IZkChildListener() {
		      @Override
		      public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
		    System.out.println(parentPath + " parentPath");
		    for (String path : currentChildren) {
		        System.out.println(path);
		    }
		      }
		  };
		  //为指定node添加监听
		  zkClient.subscribeDataChanges(path, dataListener);
		  zkClient.subscribeChildChanges("/", childListener);
		  //zkclient操作Zookeeper服务
		  ///检测node是否存在
		  if (zkClient.exists("/zookeeper")) {
		      System.out.println("Exist zookeeper path");
		  } else {
		      System.out.println("Not Exist zookeeper path");
		  }
		  zkClient.createPersistent(path, path.getBytes());
		  byte[] before = zkClient.readData(path);
		  System.out.println("before:" + new String(before));
		  //以原子的方式更新指定的path node的数据
		  zkClient.cas(path, new IZkClient.DataUpdater() {
		      @Override
		      public byte[] update(byte[] currentData) {
		    return new String(currentData).concat(new String(" updated")).getBytes();
		      }
		  });
		  byte[] after = zkClient.readData(path);
		  System.out.println("after:" + new String(after));
		  //取消指定path node的数据监听
		  zkClient.unsubscribeDataChanges(path, dataListener);
		  zkClient.writeData(path, "new-data".getBytes());
		  byte[] dataBytes = zkClient.readData(path);
		  String data = new String(dataBytes);
		  System.out.println(path + " data :" + data);
		  System.in.read();
		  zkClient.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	  */
	   
//	  zkServer.shutdown();
	}
}

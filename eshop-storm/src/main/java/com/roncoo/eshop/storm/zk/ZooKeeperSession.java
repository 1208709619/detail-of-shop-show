package com.roncoo.eshop.storm.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperSession {

    private static CountDownLatch connectSemphere=new CountDownLatch(1);

    private ZooKeeper zooKeeper;

    public ZooKeeperSession(){
        try {
            this.zooKeeper=new ZooKeeper("192.168.0.101:2181,192.168.0.106:2181,192.168.0.105:2181",5000,new ZooKeeperWatcher());

            try {
                // CountDownLatch
                // java多线程并发同步的一个工具类
                // 会传递进去一些数字，比如说1,2 ，3 都可以
                // 然后await()，如果数字不是0，那么久卡住，等待

                // 其他的线程可以调用coutnDown()，减1
                // 如果数字减到0，那么之前所有在await的线程，都会逃出阻塞的状态
                // 继续向下运行

                connectSemphere.await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ZooKeeper session established......");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void acquireDistributedLock(){
        String path = "/taskid-list-lock" ;

        try {
            zooKeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
            System.out.println("success to acquire lock for taskid-list-lock");
        } catch (Exception e) {
            // 如果那个商品对应的锁的node，已经存在了，就是已经被别人加锁了，那么就这里就会报错
            // NodeExistsException
            int count= 0;
            while (true){
                try {
                    Thread.sleep(20);
                    zooKeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
                } catch (Exception e1) {
                    count++;
                    System.out.println("the "+count+" times try to acquire lock for taskid-list-lock......");
                    continue;
                }
                System.out.println("success to acquire lock for taskid-list-lock after "+count+" times to try.....");
                break;
            }
        }
    }



    /**
     * 释放掉一个分布式锁
     * @param
     */
    public void releaseDistributedLock() {
        String path = "/taskid-list-lock" ;
        try {
            zooKeeper.delete(path, -1);
            System.out.println("release the lock for taskid-list-lock.....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNodeData(String path){
        try {
            String data = new String(zooKeeper.getData(path,false,new Stat()));
            return data;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }


    public void setNodeData(String path,String data){
        try {
            zooKeeper.setData(path,data.getBytes(),-1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createNode(String path){
        try {
           zooKeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ZooKeeperWatcher implements Watcher{

        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("Receive watched event:"+watchedEvent);
            if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
                connectSemphere.countDown();
            }
        }
    }

    /**
     * 封装单例的静态内部类
     * @author Administrator
     *
     */
    private static class Singleton {

        private static ZooKeeperSession instance;

        static {
            instance = new ZooKeeperSession();
        }

        public static ZooKeeperSession getInstance() {
            return instance;
        }

    }

    /**
     * 获取单例
     * @return
     */
    public static ZooKeeperSession getInstance() {
        return Singleton.getInstance();
    }

    /**
     * 初始化单例的便捷方法
     */
    public static void init() {
        getInstance();
    }
}

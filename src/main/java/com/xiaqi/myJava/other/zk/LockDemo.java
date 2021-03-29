package com.xiaqi.myJava.other.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryOneTime;

public class LockDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryOneTime(1000));
        client.start();
        // 定义锁节点名称
        InterProcessMutex lock = new InterProcessMutex(client, "/locks/my_lock");

        // 加锁
        lock.acquire();

        // 业务逻辑代码...

        // 释放锁
        lock.release();
    }
}

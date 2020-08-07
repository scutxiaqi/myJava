package com.xiaqi.myJava.zk;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 自己实现的分布式锁例子，比较简陋，相当于hello world
 *
 */
public class LockSample {

    // ZooKeeper配置信息
    private ZooKeeper zk;
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";
    private String lockPath;

    // 监控lockPath的前一个节点的watcher
    private Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            System.out.println(event.getPath() + " 前锁释放");
            synchronized (this) {
                notifyAll();
            }

        }
    };

    public LockSample() throws IOException {
        // 创建ZooKeeper对象时，只要对象完成初始化便立刻返回。建立连接是以异步的形式进行的，当连接成功建立后，会回调watcher的process方法。
        zk = new ZooKeeper("localhost:2181", 10000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("失去连接");

                }
            }
        });
    }

    // 获取锁的原语实现.
    public void acquireLock() throws InterruptedException, KeeperException {
        // 创建锁节点
        createLock();
        // 尝试获取锁
        attemptLock();
    }

    // 创建锁的原语实现。在lock节点下创建该线程的锁节点
    private void createLock() throws KeeperException, InterruptedException {
        Stat stat = zk.exists(LOCK_ROOT_PATH, false);
        if (stat == null) {// 如果根节点不存在，则创建根节点
            zk.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 创建EPHEMERAL_SEQUENTIAL类型节点
        String lockPath = zk.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, Thread.currentThread().getName().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(Thread.currentThread().getName() + " 锁创建: " + lockPath);
        this.lockPath = lockPath;
    }

    private void attemptLock() throws KeeperException, InterruptedException {
        // 获取Lock所有子节点，按照节点序号排序
        List<String> lockPaths = zk.getChildren(LOCK_ROOT_PATH, false);

        Collections.sort(lockPaths);

        int index = lockPaths.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));

        // 如果lockPath是序号最小的节点，则获取锁
        if (index == 0) {
            System.out.println(Thread.currentThread().getName() + " 锁获得, lockPath: " + lockPath);
            return;
        } else {
            // lockPath不是序号最小的节点，监控前一个节点
            String preLockPath = lockPaths.get(index - 1);

            Stat stat = zk.exists(LOCK_ROOT_PATH + "/" + preLockPath, watcher);

            // 假如前一个节点不存在了，比如说执行完毕，或者执行节点掉线，重新获取锁
            if (stat == null) {
                attemptLock();
            } else { // 阻塞当前进程，直到preLockPath释放锁，被watcher观察到，notifyAll后，重新acquireLock
                System.out.println(" 等待前锁释放，prelocakPath：" + preLockPath);
                synchronized (watcher) {
                    watcher.wait();
                }
                attemptLock();
            }
        }
    }

    // 释放锁的原语实现
    public void releaseLock() throws KeeperException, InterruptedException {
        zk.delete(lockPath, -1);
        zk.close();
        System.out.println(" 锁释放：" + lockPath);
    }

}

package com.xiaqi.myJava.java.concurrent.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;

import sun.misc.Unsafe;

public class AQS {

	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long stateOffset;
	private static final long headOffset;
	private static final long tailOffset;
	private static final long waitStatusOffset;

	static {
		try {
			stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
			headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
			tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
			waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	/**
	 * 当前持有锁的线程.实际上是一个虚节点，本身并不会存储线程信息。
	 */
	private transient volatile Node head;

	private transient volatile Node tail;

	private volatile int state;
	private transient Thread exclusiveOwnerThread;

	protected final int getState() {
		return state;
	}

	protected final void setState(int newState) {
		state = newState;
	}

	protected final void setExclusiveOwnerThread(Thread thread) {
		exclusiveOwnerThread = thread;
	}

	protected final Thread getExclusiveOwnerThread() {
		return exclusiveOwnerThread;
	}

	/**
	 * CAS比较并交换，原子性操作。与预期值一致，返回true。否则false
	 * 
	 * @param expect 预期值
	 * @param update 替换值
	 * @return
	 */
	protected final boolean compareAndSetState(int expect, int update) {
		return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
	}

	private final boolean compareAndSetHead(Node update) {
		return unsafe.compareAndSwapObject(this, headOffset, null, update);
	}

	private final boolean compareAndSetTail(Node expect, Node update) {
		return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
	}

	private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
		return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
	}

	public final void acquire(int arg) {
		if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
			selfInterrupt();
	}

	/**
	 * 在队列尾部新增一个节点。
	 */
	private Node addWaiter(Node mode) {
		Node node = new Node(Thread.currentThread(), mode);// 创建一个当前线程的节点
		Node pred = tail;
		if (pred != null) {
			node.prev = pred;// 该节点的前趋指针指向tail
			if (compareAndSetTail(pred, node)) {// 将尾指针指向该节点
				pred.next = node;// 旧尾部节点的next指针指向该节点.
				return node;
			}
		}
		enq(node);// cas失败,或在pred == null时调用
		return node;
	}

	/**
	 * 在队列尾部新增一个节点.无限循环,不停的尝试,直到成功
	 * 
	 * @param node
	 * @return
	 */
	private Node enq(final Node node) {
		for (;;) {// 无限循环,不停的尝试
			Node t = tail;
			if (t == null) {
				if (compareAndSetHead(new Node()))
					tail = head;// head是一个哨兵的作用,并不代表某个要获取锁的线程节点
			} else {
				node.prev = t;
				if (compareAndSetTail(t, node)) {
					t.next = node;
					return t;
				}
			}
		}
	}

	final boolean acquireQueued(final Node node, int arg) {
		boolean failed = true;
		try {
			boolean interrupted = false;
			for (;;) {// 无限循环,直到获取锁.
				final Node p = node.predecessor();
				// node的前驱是head,就说明,node是将要获取锁的下一个节点.
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null; // help GC
					failed = false;
					return interrupted;
				}
				if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
					interrupted = true;
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	private void setHead(Node node) {
		head = node;
		node.thread = null;
		node.prev = null;
	}

	private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
		int ws = pred.waitStatus;
		if (ws == Node.SIGNAL)
			/*
			 * This node has already set status asking a release to signal it, so it can
			 * safely park.
			 */
			return true;
		if (ws > 0) {
			/*
			 * Predecessor was cancelled. Skip over predecessors and indicate retry.
			 */
			do {
				node.prev = pred = pred.prev;
			} while (pred.waitStatus > 0);
			pred.next = node;
		} else {
			/*
			 * waitStatus must be 0 or PROPAGATE. Indicate that we need a signal, but don't
			 * park yet. Caller will need to retry to make sure it cannot acquire before
			 * parking.
			 */
			compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
		}
		return false;
	}

	private final boolean parkAndCheckInterrupt() {
		LockSupport.park(this);
		return Thread.interrupted();
	}

	private void cancelAcquire(Node node) {

	}

	/**
	 * 尝试获取锁，成功返回true，失败返回false
	 */
	protected boolean tryAcquire(int arg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 中断当前线程
	 */
	static void selfInterrupt() {
		Thread.currentThread().interrupt();
	}

	static final class Node {
		static final Node SHARED = new Node(); // 是否是共享模式
		static final Node EXCLUSIVE = null; // 排他模式

		static final int CANCELLED = 1; // 取消状态
		static final int SIGNAL = -1; // 等待触发状态
		static final int CONDITION = -2; // 等待唤醒条件
		static final int PROPAGATE = -3; // 节点状态需要向后传播
		volatile Node prev;
		/**
		 * 后继节点
		 */
		volatile Node next;
		/**
		 * 节点对应的线程
		 */
		volatile Thread thread;
		/**
		 * 等待队列中的后继节点
		 */
		Node nextWaiter;

		volatile int waitStatus;// 等待状态

		Node() {
		}

		Node(Thread thread, Node mode) { // Used by addWaiter
			this.nextWaiter = mode;
			this.thread = thread;
		}

		final Node predecessor() throws NullPointerException {
			Node p = prev;
			if (p == null)
				throw new NullPointerException();
			else
				return p;
		}
	}
}

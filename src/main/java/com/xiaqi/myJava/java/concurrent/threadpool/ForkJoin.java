package com.xiaqi.myJava.java.concurrent.threadpool;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join是一个并行计算框架：把一个大任务拆成多个小任务并行执行，最后归总。<br/>
 * 第一个阶段分解任务，把任务分解为一个个小任务直至小任务可以简单的计算返回结果。<br/>
 * 第二阶段合并结果，把每个小任务的结果合并返回得到最终结果
 * <br>
 * Fork/Join用的不多，只能在JVM虚拟机内部进行并行计算，也就是只属能在一台机器上做并行计算。<br>
 * 一般只有在数据处理量非常大的时候才会去选择并行计算，这时候往往是用hadoop这样的分布式map/reduce框架，在多台机器上做，这样性能会更好，而且容易扩展。<br>
 */
public class ForkJoin {
	/**
	 * 对超大数组求和。 1.把数组拆成两部分，分别计算，最后加起来就是最终结果 2.拆成两部分还是很大，我们还可以继续拆，用4个线程并行执行 3.以此类推
	 */
	public static void main(String[] args) throws Exception {
		long[] array = new long[10000000];// 创建2000个随机数组成的数组:
		long sum = 0; // 数组求和的值
		for (int i = 0; i < array.length; i++) {
			array[i] = random();
		}
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
		long endTime = System.currentTimeMillis();
		System.out.println("数组求和的值: " + sum + " in " + (endTime - startTime) + " ms.");

		ForkJoinTask<Long> task = new MyTask(array, 0, array.length);
		startTime = System.currentTimeMillis();
		ForkJoinPool pool = ForkJoinPool.commonPool();// 治理分治任务的线程池
		Long result = pool.invoke(task);// Fork/Join线程池调用任务
		endTime = System.currentTimeMillis();
		System.out.println("Fork/join sum: " + result + " in " + (endTime - startTime) + " ms.");
	}

	static Random random = new Random(0);

	static long random() {
		return random.nextInt(10000);
	}
}

/**
 * 分治任务，等同于平日用的Runnable
 */
class MyTask extends RecursiveTask<Long> {
	private static final long serialVersionUID = -8582646073755071324L;
	static final int THRESHOLD = 1000000;
	long[] array;
	int start;
	int end;

	MyTask(long[] array, int start, int end) {
		this.array = array;
		this.start = start;
		this.end = end;
	}

	/**
	 * 任务执行的主要计算，等同于Runnable中run()方法<br/>
	 * 判断一个任务是否足够小，如果是，直接计算，否则，就分拆成几个小任务分别计算。这个过程可以反复“裂变”成一系列小任务。
	 */
	@Override
	protected Long compute() {
		if (end - start <= THRESHOLD) { // 如果任务足够小,直接计算:
			long sum = 0;
			for (int i = start; i < end; i++) {
				sum += this.array[i];
			}
			return sum;
		}
		int middle = (end + start) / 2;// 任务太大,一分为二
		// 此处为递归调用
		MyTask subtask1 = new MyTask(this.array, start, middle);
		MyTask subtask2 = new MyTask(this.array, middle, end);
		invokeAll(subtask1, subtask2);
		// 获得子任务的结果
		Long subresult1 = subtask1.join();
		Long subresult2 = subtask2.join();
		Long result = subresult1 + subresult2;// 汇总结果
		//System.out.println("result = " + subresult1 + " + " + subresult2 + " ==> " + result);
		return result;
	}
}

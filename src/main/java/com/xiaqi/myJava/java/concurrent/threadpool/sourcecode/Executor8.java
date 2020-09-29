package com.xiaqi.myJava.java.concurrent.threadpool.sourcecode;

/**
 * 执行器接口，也是最顶层的抽象核心接口， 分离了任务和任务的执行。
 */
public interface Executor8 {
    /**
     * 执行给定的Runnable任务.<br/>
     * 根据Executor的实现不同, 具体执行方式也不相同.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be accepted for execution
     * @throws NullPointerException       if command is null
     */
    void execute(Runnable command);
}

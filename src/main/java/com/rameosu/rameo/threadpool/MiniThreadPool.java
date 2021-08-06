package com.rameosu.rameo.threadpool;

import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mini线程池
 *
 * @author suwei
 * @version 1.0
 * @date 2021/7/31 10:01
 */
public class MiniThreadPool {

    /**
     * 阻塞任务队列
     */
    private final BlockingQueue<Task> taskQueue;

    /**
     * Hash表去持有工作线程的引用
     */
    private final HashSet<Worker> workers = new HashSet<>();

    /**
     * 线程池当前状态
     */
    private ThreadPoolStatus status;

    /**
     * 构造器
     *
     * @param workNumber 工作线程数
     * @param taskQueue  任务队列
     */
    public MiniThreadPool(int workNumber, BlockingQueue<Task> taskQueue) {
        this.taskQueue = taskQueue;
        status = ThreadPoolStatus.RUNNING;
        for (int i = 0; i < workNumber; i++) {
            workers.add(new Worker("Worker-" + i, taskQueue));
        }
        for (Worker worker : workers) {
            // 创建工作线程
            Thread thread = new Thread(worker);
            thread.setName(worker.getName());
            // 工作线程进入Runnable状态
            thread.start();
        }
    }

    /**
     * 线程池执行方法，使用synchronized加锁
     *
     * @param task
     */
    public synchronized void execute(Task task) {
        if (!this.status.isRunning()) {
            System.out.println("当前线程池非运行状态，无法执行任务");
            return;
        }
        // 工作队列已满的操作，这里直接返回，实际开发中可以使用线程池的拒绝策略也可以重试
        if (!this.taskQueue.offer(task)) {
            System.out.println("任务队列已满，未执行：" + task.getTaskDesc());
        }
    }

    /**
     * 等待所有的任务都执行完毕
     */
    public synchronized void waitUntilTaskFinished() {
        while (!this.taskQueue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 线程池关闭
     */
    public synchronized void stop() {
        this.status = ThreadPoolStatus.STOP;
        for (Worker worker : workers) {
            worker.stop();
        }
    }

    /**
     * 任务对象
     */
    interface Task extends Runnable {
        /**
         * 获取任务详情
         *
         * @return
         */
        String getTaskDesc();
    }

    /**
     * 用于执行任务的工作线程
     */
    @Data
    class Worker implements Runnable {

        private final String name;

        private Thread thread = null;

        private final BlockingQueue<Task> taskQueue;

        private boolean isRunning = true;

        private AtomicInteger counter = new AtomicInteger();

        public Worker(String name, BlockingQueue<Task> taskQueue) {
            this.name = name;
            this.taskQueue = taskQueue;
        }

        @Override
        public void run() {
            this.thread = Thread.currentThread();
            while (isRunning) {
                // 取出一个任务
                Task task = taskQueue.poll();
                if (task != null) {
                    System.out.println(this.thread.getName() + "：获取到新任务 -> " + task.getTaskDesc());
                    // 执行执行
                    task.run();
                    counter.incrementAndGet();
                }
            }
            System.out.println(this.thread.getName() + "：已结束工作，执行任务数量：" + counter.get());
        }

        /**
         * 中断工作线程
         */
        public void stop() {
            isRunning = false;
            if (this.thread != null) {
                this.thread.interrupt();
            }
        }

        public String getName() {
            return name;
        }
    }

    enum ThreadPoolStatus {
        /**
         * 运行状态
         * 线程池被创建后的初始状态，能接受新提交的任务，并且也能处理阻塞队列中的任务
         */
        RUNNING,
        /**
         * 关闭状态
         * 关闭状态，不再接受新提交的任务，但仍可以继续处理已进入阻塞队列中的任务
         */
        SHUTDOWN,
        /**
         * 中断状态
         * 中断正在处理任务的线程，不能再接受新任务，也不继续处理队列中的任务
         */
        STOP,
        /**
         * 整理状态
         * 所有的任务都已终止，workerCount(有效工作线程数)为0
         */
        TIDYING,
        /**
         * 终止状态
         * 线程池彻底终止运行
         */
        TERMINATED;

        public boolean isRunning() {
            return RUNNING.equals(this);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        MiniThreadPool threadPool = new MiniThreadPool(3, new LinkedBlockingQueue<>(8));
        String[] works = {"打扫", "洗衣服", "做饭", "敲代码", "休息", "打游戏", "看书", "运动"};
        for (String work : works) {
//            Thread.sleep(100);
            threadPool.execute(new Task() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "：" + work);
                    // 下面是你真正要执行的业务逻辑
                    calc();
                }

                @Override
                public String getTaskDesc() {
                    return work;
                }
            });
        }
        threadPool.waitUntilTaskFinished();
        threadPool.stop();
    }

    private static void calc() {
        System.out.println("计算 ... ...");
    }
}

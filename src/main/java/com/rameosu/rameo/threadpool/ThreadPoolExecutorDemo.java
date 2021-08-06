package com.rameosu.rameo.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * TODO
 *
 * @author suwei
 * @version 1.0
 * @date 2021/8/5 16:27
 */
public class ThreadPoolExecutorDemo {
    @SneakyThrows
    public static void main(String[] args) {
        // 给线程定义有业务含义的名称
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("thread-%s").build();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10,  // 核心线程数
                10,  // 最大线程数
                1000,  // 空闲线程最大等待时间
                TimeUnit.MILLISECONDS,  // 时间单位，毫秒
                new LinkedBlockingQueue<>(50),  // 工作线程等待队列
                threadFactory,  // 自定义线程工厂
                new ThreadPoolExecutor.AbortPolicy());  // 线程池满时的拒绝策略

        for (int i = 0; i < 100; i++) {
//            Thread.sleep(50);
            threadPoolExecutor.execute(new Task());
        }
    }

    static class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "：do something");
        }
    }
}

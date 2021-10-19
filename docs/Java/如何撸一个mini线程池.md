# 如何撸一个mini线程池

## 线程池应该有哪些基础功能？

- 线程池状态管理
- 线程池初始化
- 工作线程管理
- 任务队列
- 任务提交
- 任务执行

### 线程池状态管理

在mini版的线程池中不需要像`ThreadPoolExecutor`中的线程池一样拥有5种状态，只需要：`RUNNING`和`STOP`即可。

### 线程池初始化

线程的初始化只需提供`工作线程数`和`任务队列`

### 工作线程管理

1. 简单的初始化：`工作线程数`和`任务队列`
2. 重写`run`方法
3. 工作线程状态管理：`RUNNING`和`STOP`
4. 原子计数器记录工作线程执行的任务数

### 任务队列

使用阻塞队列，指定队列大小

### 任务提交

通过队列的`offer`方法添加任务到队列，队列饱和时，唤醒拒绝策略

### 任务执行

线程池初始化时就创建好设定的工作线程数量的线程，并调用线程的`start`方法，使工作线程进入`RUNNABLE`状态；

工作线程获取到系统资源时，会执行重写的线程`run`方法，该方法通过自旋不停从任务队列中取出任务，有任务时则执行该任务的`run`方法。

## 代码

```java
public class MiniThreadPool {
    /**
     * 阻塞任务队列
     */
    private final BlockingQueue<Task> taskQueue;

    private final List<Worker> workers = new ArrayList<>();

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
            workers.add(new Worker("Worker" + i, taskQueue));
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
            System.out.println("任务队列已满");
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
         * @return
         */
        String getTaskDesc();
    }

    /**
     * 用于执行任务的工作线程
     */
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
         * 中断状态
         * 中断正在处理任务的线程，不能再接受新任务，也不继续处理队列中的任务
         */
        STOP;

        public boolean isRunning() {
            return RUNNING.equals(this);
        }
    }

    public static void main(String[] args) {
        MiniThreadPool threadPool = new MiniThreadPool(3, new LinkedBlockingQueue<>(3));
        String[] works = {"打扫", "洗衣服", "做饭", "敲代码", "休息", "打游戏", "看书"};
        for (String work : works) {
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
```

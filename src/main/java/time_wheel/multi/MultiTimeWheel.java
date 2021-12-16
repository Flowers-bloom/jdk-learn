package main.java.time_wheel.multi;

import main.java.time_wheel.HashedWheelBucket;
import main.java.time_wheel.TimeTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 两级时间轮：秒 + 分钟
 * 1. 添加延时任务时，如果到期时间超出了当前时间轮范围，则添加到下一级时间轮
 * 2. 前 n-1 级时间轮的循环指针到达了范围上限时，从下一级时间轮里加载延时任务；
 * 第 n 级的时间轮到达了范围上限时，继续循环后移
 *
 * 优化策略：
 * 始末时刻需要加载未来一小时或者一天的数据会导致延迟以及内存可能不够的问题
 * 可以考虑在时刻到来之前对数据进行预加载，加载未来的一部分数据
 * 从而避免加载延迟和内存不够的问题
 */
public class MultiTimeWheel {
    private static final int SEC_TICK = 1000;
    private static final int MIN_TICK = 1000 * SEC_TICK;
    private static final int CAP = 60;
    private final Object lock = new Object();
    // 工作线程池
    private final ExecutorService executor = Executors.newCachedThreadPool();
    // 秒、分钟时间轮
    private final HashedWheelBucket[] secWheel = new HashedWheelBucket[CAP];
    private final HashedWheelBucket[] minWheel = new HashedWheelBucket[CAP];
    // 指针
    private int secCur;
    private int minCur;
    // 当前延时任务个数
    private final AtomicInteger size = new AtomicInteger(0);

    public MultiTimeWheel() {
        for (int i=0; i<CAP; i++) {
            secWheel[i] = new HashedWheelBucket();
            minWheel[i] = new HashedWheelBucket();
        }
    }

    /**
     * 添加任务
     * @param task 任务
     * @return 是否添加成功
     */
    public boolean addTask(TimeTask task) {
        if (task == null || task.delayTimeSec > CAP * CAP) {
            return false;
        }
        int i = size.get();
        // 写入任务
        writeWheel(task);
        size.incrementAndGet();

        // 确认条件下，再尝试争用锁唤醒时间轮线程
        // 避免因为锁争用导致添加延时任务被推迟
        if (i == 0 || size.get() == 0) {
            synchronized (lock) {
                lock.notify();
            }
        }
        return true;
    }

    private void writeWheel(TimeTask task) {
        boolean inSecWheel = true;
        int idx = secCur + task.delayTimeSec;
        if (idx > CAP) {
            inSecWheel = false;
            // [61, 120] +1
            // [121, 180] +2
            task.delayTimeSec = idx % CAP;
            idx = minCur + (idx-1) / CAP;
        }

        if (inSecWheel) {
            synchronized (secWheel[idx].lock) {
                secWheel[idx].offer(task);
            }
        }else {
            synchronized (minWheel[idx].lock) {
                minWheel[idx].offer(task);
            }
        }
    }

    public void start() {
        new Thread(this::run, "run thread").start();
        System.out.println("MultiTimeWheel run thread start");
    }

    private void run() {
        try {
            for (;;) {
                synchronized (lock) {
                    // 为了避免虚假唤醒，这里不能用 if
                    while (size.get() == 0) {
                        lock.wait();
                    }
                    secCur++;
                    if (secCur == CAP) {
                        secCur = 0;
                    }
                    Thread.sleep(SEC_TICK);

                    HashedWheelBucket bucket = secWheel[secCur];
                    System.out.printf("cur: %d, size: %d, bucketSize:%d\n", secCur, size.get(), bucket.size);
                    synchronized (bucket.lock) {
                        while (!bucket.isEmpty()) {
                            TimeTask task = bucket.poll();
                            size.decrementAndGet();
                            System.out.println("exec sec: " + secCur);
                            executor.execute(task.runnable); // dispatch worker thread to exec task
                        }
                    }
                    if (secCur == 0)
                        loadNext(); // 加载未来一分钟的延时任务
                }
            }
        } catch (InterruptedException e) {
            System.out.println("MultiTimeWheel run thread exit, error: " + e);
        } finally {
            executor.shutdown();
        }
    }

    private void loadNext() {
        HashedWheelBucket bucket = minWheel[++minCur];
        synchronized (bucket.lock) {
            while (!bucket.isEmpty()) {
                TimeTask task = bucket.poll();
                secWheel[task.delayTimeSec].offer(task);
            }
        }
    }
}

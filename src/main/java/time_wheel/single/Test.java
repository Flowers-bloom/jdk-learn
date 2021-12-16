package main.java.time_wheel.single;

import main.java.time_wheel.TimeTask;

import java.io.IOException;
import java.time.LocalTime;

/**
 * 单级时间轮测试：目前存在锁争用导致写入的任务延后才执行
 * 已优化锁争用问题，功能符合预期
 */
public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
        SingleTimeWheel timeWheel = new SingleTimeWheel();
        timeWheel.start();

        int[] arr = new int[]{
                8, 12, 13, 12, 16, 16, 25
        };
        new Thread(() -> {
            for (int i : arr) {
                TimeTask task = new TimeTask(() -> {
                    System.out.println(LocalTime.now() + ": exec task " + i);
                }, i);
                timeWheel.addTask(task);
            }
        }).start();
        //Thread.sleep(15 * 1000);

        new Thread(() -> {
            for (int i : arr) {
                TimeTask task = new TimeTask(() -> {
                    System.out.println(LocalTime.now() + ": exec task " + i);
                }, i);
                timeWheel.addTask(task);
            }
        }).start();
        System.in.read();
    }
}

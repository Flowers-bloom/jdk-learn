package main.java.time_wheel.multi;

import main.java.time_wheel.TimeTask;

import java.io.IOException;
import java.time.LocalTime;

/**
 * 多级时间轮测试
 */
public class Test {
    public static void main(String[] args) throws InterruptedException, IOException {
        MultiTimeWheel timeWheel = new MultiTimeWheel();
        timeWheel.start();

        int[] arr = new int[] {
                3, 24, 67, 115, 162, 163
        };
        new Thread(() -> {
            for (int i : arr) {
                TimeTask task = new TimeTask(() -> {
                    System.out.println(LocalTime.now() + ": first exec task [" + i + "]");
                }, i);
                timeWheel.addTask(task);
            }
        }).start();
        Thread.sleep(15 * 1000);

        new Thread(() -> {
            for (int i : arr) {
                TimeTask task = new TimeTask(() -> {
                    System.out.println(LocalTime.now() + ": second exec task [" + i + "]");
                }, i);
                timeWheel.addTask(task);
            }
        }).start();
        // 阻塞
        System.in.read();
    }
}

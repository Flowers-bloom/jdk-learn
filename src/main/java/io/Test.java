package main.java.io;

import java.io.IOException;

public class Test {
    /**
     * main线程退出后，如果当前只存在其他的守护线程，则程序会直接退出；
     * 如果存在非守护线程，则会等待其他守护线程执行完毕，这是jvm底层实现机制
     */
    public static void main(String[] args) throws IOException {
        System.out.println("main start");
        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1 exit");
        });
        t1.setDaemon(true);
        t1.start();
        System.out.println("main exit");
    }
}

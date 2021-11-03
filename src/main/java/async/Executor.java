package main.java.async;


public class Executor<T> {

    public static void main(String[] args) {
        Executor<Integer> executor = new Executor<>();
        executor.submit(() -> 12).addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("task finish callback! result = " + f.get());
            }
        });

        Executor<String> executor1 = new Executor<>();
        executor1.submit(() -> "xxx").addListener(f1 -> {
            if (f1.isSuccess()) {
                System.out.println("task finish callback! result = " + f1.get());
            }
        });
        System.out.println("main end");
    }

    public Future<T> submit(Task<T> task) {
        Future<T> future = new Future<>();
        Thread thread = new Thread(() -> {
            if (conditionReady()) {
                T result = task.action();
                future.setResult(result);
                future.callbackListener(future);
            }
        }, "Async");
        thread.start();
        return future;
    }

    private boolean conditionReady() {
        try {
            System.out.println("wait condition ready");
            Thread.sleep(1000);
            System.out.println("condition ready");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}

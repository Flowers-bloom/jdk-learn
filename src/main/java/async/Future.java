package main.java.async;


public class Future<T> {
    private Listener listener;
    private T result;
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setResult(T result) {
        this.result = result;
        success = true;
    }

    public Object get() {
        return result;
    }

    public void addListener(Listener listener) {
        this.listener = listener;
    }

    public void callbackListener(Future<?> future) {
        listener.taskComplete(future);
    }
}

package main.java.io.reactor.ms;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 从 Reactor
 */
public class SubReactor {
    private final SelectionKey key;
    private final ExecutorService executor;

    public SubReactor(SelectionKey k) {
        key = k;
        executor = Executors.newCachedThreadPool();
    }

    public void process() {
        WorkHandler workHandler = new WorkHandler(key, executor);
        workHandler.handle();
    }
}

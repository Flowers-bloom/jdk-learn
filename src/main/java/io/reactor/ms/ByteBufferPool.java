package main.java.io.reactor.ms;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class ByteBufferPool {
    private final int cap;
    private final int bufferSize = 256;
    private final Queue<ByteBuffer> pool;

    public ByteBufferPool() {
        this(10);
    }

    public ByteBufferPool(int initialCap) {
        cap = initialCap;
        pool = new LinkedList<>();
        initPool();
    }

    private void initPool() {
        for (int i=0; i<cap; i++) {
            pool.offer(ByteBuffer.allocate(bufferSize));
        }
    }

    public boolean returnObject(ByteBuffer buffer) {
        return pool.offer(buffer);
    }

    public ByteBuffer borrowObject() {
        ByteBuffer buffer = pool.poll();
        if (buffer == null) {
            buffer = ByteBuffer.allocate(bufferSize);
        }
        return buffer;
    }
}

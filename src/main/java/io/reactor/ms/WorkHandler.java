package main.java.io.reactor.ms;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public class WorkHandler {
    private final SelectionKey key;
    private final ExecutorService executor;
    private final ByteBufferPool pool;

    public WorkHandler(SelectionKey sk, ExecutorService es) {
        key = sk;
        executor = es;
        pool = new ByteBufferPool();
    }

    public void handle() {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            if (!channel.isConnected()) {
                System.out.println(channel.getRemoteAddress() + " close connect");
                return;
            }

            ByteBuffer b1 = pool.borrowObject();
            int n = channel.read(b1); // 执行 read 之后，selector.select() 才不会再次监听到同一个读事件
            executor.execute(() -> {
                try {
                    if (n > 0) {
                        String tn = Thread.currentThread().getName();
                        String str = String.format("%s %s ", "[MSReactor]", tn);
                        ByteBuffer b2 = pool.borrowObject();
                        b2.put(str.getBytes());
                        b1.flip();  // reset read pos
                        b2.put(b1);
                        b2.flip();  // reset read pos
                        channel.write(b2);
                        b1.clear();
                        b2.clear();
                        pool.returnObject(b1);
                        pool.returnObject(b2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            try {
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}

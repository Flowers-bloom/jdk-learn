package main.java.io.reactor.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public class WorkHandler {
    private final SelectionKey key;
    private final ExecutorService executor;
    private final ByteBuffer buffer;

    public WorkHandler(SelectionKey sk, ExecutorService es) {
        key = sk;
        executor = es;
        buffer = ByteBuffer.allocate(1024);
    }

    public void handle() {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            int n = channel.read(buffer); // 执行 read 之后，selector.select() 才不会再次监听到同一个读事件
            executor.execute(() -> {
                try {
                    System.out.println("work handle call");
                    if (n > 0) {
                        String tn = Thread.currentThread().getName();
                        String str = String.format("%s %s %s",
                                "[SingleReactor]", tn, new String(buffer.array()).trim());
                        channel.write(ByteBuffer.wrap(str.getBytes()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

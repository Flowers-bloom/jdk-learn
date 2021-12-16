package main.java.netty_example.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerBootstrap {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private Channel channel;
    private AbstractChannelInitializer initializer;

    public ServerBootstrap() {
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }

    public ServerBootstrap handler(AbstractChannelInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    public ChannelFuture bind(int port) {
        ChannelFuture future = new ChannelFuture();
        executor.execute(() -> doBind(port, future));
        return future;
    }

    public void doBind(int port, ChannelFuture future) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverSocketChannel.bind(new InetSocketAddress(port));

            for (;;) {
                selector.select();

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        // init and register channel
                        channel = new Channel(socketChannel, initializer.getPipeline());
                        initializer.init();
                        initializer.getPipeline().setChannel(channel);
                        // future callback
                        future.setChannel(channel);
                        future.callbackListener();

                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int n = socketChannel.read(buffer);
                        if (n != -1) {
                            if (n > 1024) {
                                throw new BufferOverflowException();
                            }
                            initializer.getPipeline()
                                    .process(ByteBuffer.wrap(buffer.array(), 0, n));
                        }
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

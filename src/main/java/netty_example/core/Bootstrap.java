package main.java.netty_example.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Bootstrap {
    private Channel channel;
    private AbstractChannelInitializer initializer;

    public Bootstrap handler(AbstractChannelInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    public ChannelFuture connect(String address, int port) {
        ChannelFuture future = new ChannelFuture();
        Thread connectThread = new Thread(() -> {
            doConnect(address, port, future);
        });
        connectThread.start();
        return future;
    }

    private void doConnect(String address, int port, ChannelFuture future) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(address, port));

            for (;;) {
                selector.select(); // select 必须和 socket 操作在同一个线程，否则会阻塞
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isConnectable()) {
                        SocketChannel socket = (SocketChannel) key.channel();
                        if (socket.isConnectionPending()) {
                            socket.finishConnect();
                        }
                        // init and register channel
                        channel = new Channel(socket, initializer.getPipeline());
                        initializer.init();
                        initializer.getPipeline().setChannel(channel);

                        // future callback
                        future.setChannel(channel);
                        future.callbackListener();
                        // write msg
                        openChannelWriter(channel);

                        socket.configureBlocking(false);
                        socket.register(selector, SelectionKey.OP_READ);
                    }else if (key.isReadable()) {
                        SocketChannel socket = (SocketChannel) key.channel();
                        // read msg
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int n = socket.read(buffer);
                        if (n > 0) {
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

    private void openChannelWriter(Channel channel) {
        Thread writer = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            String in = sc.nextLine();
            while (!in.equals("exit")) {
                channel.writeAndFlush(in.trim());
                in = sc.nextLine();
            }
        });
        writer.start();
    }
}

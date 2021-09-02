package main.java.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * nio server
 */
public class NioServer {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false); // non-blocking mode
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            serverSocketChannel.bind(address);
            System.out.println("nio server bind " + address);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // Selector注册感兴趣的Accept操作

            // 处理连接和读写事件
            for (;;) {
                selector.select(); // 阻塞直到一个请求到来
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        // 连接请求
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("connected from " + socketChannel.getRemoteAddress());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }else if (key.isReadable()) {
                        // 读写请求
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        int n = socketChannel.read(buffer);
                        if (n != -1) {
                            String str = "[NIO] " + new String(buffer.array()).trim();
                            socketChannel.write(ByteBuffer.wrap(str.getBytes()));
                        }else {
                            socketChannel.close();
                        }
                    }
                    it.remove(); // 处理完后移除
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

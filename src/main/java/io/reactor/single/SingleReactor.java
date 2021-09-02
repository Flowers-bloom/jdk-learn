package main.java.io.reactor.single;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 单 Reactor，多工作线程
 * Single Reactor Multiple Worker Threads
 */
public class SingleReactor {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            serverSocketChannel.bind(address);
            System.out.println("SingleReactor server bind " + address);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            for (;;) {
                selector.select(); // 阻塞
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        Acceptor acceptor = new Acceptor(serverSocketChannel, selector);
                        acceptor.accept();
                    }else if (key.isReadable()) {
                        WorkHandler workHandler = new WorkHandler(key, executor);
                        workHandler.handle();
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

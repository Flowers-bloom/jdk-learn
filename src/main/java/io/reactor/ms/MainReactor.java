package main.java.io.reactor.ms;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * 主从 Reactor，多工作线程
 * Master-Slave Reactor，Multiple Worker Threads
 */
public class MainReactor {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            serverSocketChannel.bind(address);
            System.out.println("MSReactor server bind " + address);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            for (;;) {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        Acceptor acceptor = new Acceptor(serverSocketChannel, selector);
                        acceptor.accept();
                    }else if (key.isReadable()) {
                        SubReactor subReactor = new SubReactor(key);
                        subReactor.process();
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package main.java.io.reactor.single;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor {
    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public Acceptor(ServerSocketChannel ssc, Selector s) {
        serverSocketChannel = ssc;
        selector = s;
    }

    public void accept() {
        try {
            SocketChannel channel = serverSocketChannel.accept();
            System.out.println("connected from " + channel.getRemoteAddress());
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

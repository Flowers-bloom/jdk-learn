package main.java.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 客户端
 */
public class Client {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        Client client = new Client();
        client.startSocketChannelClient();
        System.out.println("end of main");
    }

    private void startSocketChannelClient() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            socketChannel.connect(address);

            for (;;) {
                selector.select();

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                            System.out.println("client connected success");
                        }
                        channel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        openChannelWriter(channel);
                    }else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        openChannelReader(channel);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openChannelWriter(SocketChannel channel) {
        Thread writer = new Thread(() -> {
            try {
                Scanner sc = new Scanner(System.in);
                String in = sc.nextLine();
                while (!in.equals("exit")) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.put(in.getBytes());
                    buffer.flip();
                    channel.write(buffer); // read data from buffer
                    in = sc.nextLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.start();
    }

    private void openChannelReader(SocketChannel channel) {
        Thread reader = new Thread(() -> {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                for (;;) {
                    buffer.clear();
                    int n = channel.read(buffer);
                    if (n > 0) {
                        byte[] bytes = buffer.array();
                        System.out.println("[NIO] " + new String(bytes, 0, n).trim());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "channel-reader thread");
        reader.start();
    }

    private void startSocketClient() {
        try {
            Socket socket = new Socket();
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            socket.connect(address);
            System.out.println("connected to " + address);
            openReader(socket); // bind server response
            Thread writer = new Thread(() -> {
                Scanner sc = new Scanner(System.in);
                String str = sc.nextLine();
                try {
                    while (!str.equals("exit")) {
                        OutputStream out = socket.getOutputStream();
                        byte[] bs = str.getBytes();
                        out.write(bs, 0, bs.length);
                        str = sc.nextLine();
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "writer-thread");
            writer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end of start");
    }

    private void openReader(Socket socket) {
        Thread bind = new Thread(() -> {
            try {
                for (;;) {
                    InputStream in = socket.getInputStream();
                    byte[] bs = new byte[1024];
                    int n;
                    while ((n = in.read(bs, 0, 1024)) != -1) {
                        System.out.println(new String(bs, 0, n));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "reader-thread");
        bind.start();
    }
}

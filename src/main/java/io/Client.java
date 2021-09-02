package main.java.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * 客户端
 */
public class Client {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        Client client = new Client();
        client.startSocketClient();
        System.out.println("end of main");
    }

    private void startSocketChannelClient() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            boolean connected = socketChannel.connect(address);
            while (!connected) {
                connected = socketChannel.finishConnect();
            }
            System.out.println("connected to remote " + socketChannel.getRemoteAddress());
            openChannelReader(socketChannel);
            System.out.println("opened reader");
            Scanner sc = new Scanner(System.in);
            String in = sc.nextLine();
            while (!in.equals("exit")) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(in.getBytes());
                buffer.flip();
                socketChannel.write(buffer); // read data from buffer
                in = sc.nextLine();
            }
            socketChannel.close(); // close channel
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openChannelReader(SocketChannel channel) {
        Thread reader = new Thread(() -> {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                for (;;) {
                    int n = channel.read(buffer);
                    if (n > 0) {
                        System.out.println(new String(buffer.array()).trim());
                        buffer.clear();
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

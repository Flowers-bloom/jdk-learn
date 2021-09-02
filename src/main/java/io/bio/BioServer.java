package main.java.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * bio server
 */
public class BioServer {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            InetSocketAddress address = new InetSocketAddress(ADDRESS, PORT);
            serverSocket.bind(address);
            System.out.println("bio server bind " + address);
            for (;;) {
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                byte[] bs = new byte[1024];
                while (in.read(bs, 0, 1024) != -1) {
                    OutputStream out = socket.getOutputStream();
                    String str = "[Bio Server] " + new String(bs).trim();
                    out.write(str.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

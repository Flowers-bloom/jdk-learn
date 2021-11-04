package main.java.netty_example.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Channel {
    private SocketChannel socketChannel;
    private ChannelPipeline pipeline;

    public Channel(SocketChannel socketChannel, ChannelPipeline pipeline) {
        this.socketChannel = socketChannel;
        this.pipeline = pipeline;
    }

    public String remoteAddress() {
        String address = "";
        try {
            address = socketChannel.getRemoteAddress().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void sendRemote(Object msg) {
        ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(msg).getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAndFlush(Object msg) {
        pipeline.encodeWrite(msg);
    }
}

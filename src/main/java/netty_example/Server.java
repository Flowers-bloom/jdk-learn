package main.java.netty_example;

import main.java.netty_example.core.AbstractChannelInitializer;
import main.java.netty_example.core.ChannelFuture;
import main.java.netty_example.core.ChannelPipeline;
import main.java.netty_example.core.ServerBootstrap;
import main.java.netty_example.handler.ServerStringHandler;
import main.java.netty_example.handler.StringDecoder;
import main.java.netty_example.handler.StringEncoder;

public class Server {
    private static final int PORT = 8000;

    public static void main(String[] args) throws InterruptedException {
        startServer();
    }

    private static void startServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.handler(new AbstractChannelInitializer() {
            @Override
            public void initChannel(ChannelPipeline pipeline) {
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new ServerStringHandler());
                pipeline.addLast(new StringEncoder());
            }
        });
        ChannelFuture future = serverBootstrap.bind(PORT);
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("client connected from " + future.channel().remoteAddress());
            }
        });
        System.out.println("start server success");
    }
}

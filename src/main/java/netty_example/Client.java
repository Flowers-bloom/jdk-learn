package main.java.netty_example;

import main.java.netty_example.core.AbstractChannelInitializer;
import main.java.netty_example.core.Bootstrap;
import main.java.netty_example.core.ChannelFuture;
import main.java.netty_example.core.ChannelPipeline;
import main.java.netty_example.handler.ClientStringHandler;
import main.java.netty_example.handler.StringDecoder;
import main.java.netty_example.handler.StringEncoder;

public class Client {
    public static void main(String[] args) {
        startClient();
    }

    private static void startClient() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.handler(new AbstractChannelInitializer() {
            @Override
            public void initChannel(ChannelPipeline pipeline) {
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new ClientStringHandler());
                pipeline.addLast(new StringEncoder());
            }
        });
        ChannelFuture future = bootstrap.connect("127.0.0.1", 8000);
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("client connect success");
            }
        });
        System.out.println("start client success");
    }
}

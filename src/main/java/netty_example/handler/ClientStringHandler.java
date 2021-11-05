package main.java.netty_example.handler;

import main.java.netty_example.core.AbstractChannelHandler;
import main.java.netty_example.core.ChannelHandlerContext;

public class ClientStringHandler extends AbstractChannelHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive call");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println("ClientStringHandler call");
        System.out.println("[server] " + msg);
    }
}

package main.java.netty_example.handler;

import main.java.netty_example.core.AbstractChannelHandler;
import main.java.netty_example.core.ChannelHandlerContext;

public class ServerStringHandler extends AbstractChannelHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println("ServerStringHandler call");
        System.out.println("[client] " + msg);

        // do echo
        ctx.channel().writeAndFlush(msg);
    }
}

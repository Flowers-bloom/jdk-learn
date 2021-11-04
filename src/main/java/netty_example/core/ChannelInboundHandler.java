package main.java.netty_example.core;

public interface ChannelInboundHandler {
    public void channelActive(ChannelHandlerContext ctx);
    public void channelRead(ChannelHandlerContext ctx, Object msg);
}

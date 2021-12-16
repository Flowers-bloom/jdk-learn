package main.java.netty_example.core;

public class ChannelHandlerContext {
    private Channel channel;
    private ChannelInboundHandler handler;
    private ChannelHandlerContext next;

    public ChannelHandlerContext(ChannelInboundHandler handler) {
        this.handler = handler;
    }

    public Channel channel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setNext(ChannelHandlerContext next) {
        this.next = next;
    }

    public ChannelHandlerContext getNext() {
        return next;
    }

    public ChannelInboundHandler getHandler() {
        return handler;
    }

    public void fireChannelActive() {
        next.handler.channelActive(next);
    }

    public void fireChannelRead(Object msg) {
        next.handler.channelRead(next, msg);
    }
}

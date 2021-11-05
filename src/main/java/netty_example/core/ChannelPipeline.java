package main.java.netty_example.core;

public class ChannelPipeline {
    private ChannelHandlerContext head;
    private ChannelHandlerContext tail;

    public ChannelPipeline() {
        head = new ChannelHandlerContext(null);
        tail = head;
    }

    public void addLast(ChannelInboundHandler handler) {
        if (head == null)
            throw new NullPointerException("head");

        ChannelHandlerContext ctx = head;
        while (ctx.getNext() != null)
            ctx = ctx.getNext();
        ctx.setNext(new ChannelHandlerContext(handler));
        tail = ctx.getNext();
    }

    public void setChannel(Channel channel) {
        ChannelHandlerContext ctx = head.getNext();
        if (ctx == null)
            throw new RuntimeException("handler is empty!");

        while (ctx != null) {
            ctx.setChannel(channel);
            ctx = ctx.getNext();
        }
    }

    public void process(Object msg) {
        ChannelHandlerContext ctx = head;
        if (ctx == null)
            throw new RuntimeException("handler is empty!");

        ctx.fireChannelActive();
        ctx.fireChannelRead(msg);
    }

    public void encodeWrite(Object msg) {
        tail.getHandler().channelRead(tail, msg);
    }
}

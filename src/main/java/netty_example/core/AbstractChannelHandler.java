package main.java.netty_example.core;

public abstract class AbstractChannelHandler<T> extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (match(msg)) {
            this.channelRead0(ctx, (T) msg);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    protected abstract void channelRead0(ChannelHandlerContext ctx, T msg);
}

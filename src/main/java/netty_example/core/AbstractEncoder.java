package main.java.netty_example.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEncoder<I> extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        List<Object> in = new ArrayList<>();
        if (match(msg)) {
            this.encoder(ctx, (I)msg, in);

            for (Object obj : in) {
                ctx.channel().sendRemote(obj);
            }
        }else {
            throw new RuntimeException("No handler catch!");
        }
    }

    protected abstract void encoder(ChannelHandlerContext ctx, I i, List<Object> in);
}
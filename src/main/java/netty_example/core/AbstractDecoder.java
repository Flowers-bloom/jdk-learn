package main.java.netty_example.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDecoder<I> extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        List<Object> list = new ArrayList<>();
        if (match(msg)) {
            this.decode(ctx, (I) msg, list);

            for (Object obj : list) {
                ctx.fireChannelRead(obj);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    protected abstract void decode(ChannelHandlerContext ctx, I i, List<Object> out);
}

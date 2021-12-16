package main.java.netty_example.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ChannelInboundHandlerAdapter implements ChannelInboundHandler {

    protected boolean match(Object msg) {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];
        return msg.getClass().getTypeName().equals(type.getTypeName()) ||
                msg.getClass().getGenericSuperclass().getTypeName().equals(type.getTypeName());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (ctx.getNext() != null) {
            ctx.fireChannelActive();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
    }
}

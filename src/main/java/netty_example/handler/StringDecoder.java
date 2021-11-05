package main.java.netty_example.handler;

import main.java.netty_example.core.BaseDecoder;
import main.java.netty_example.core.ChannelHandlerContext;

import java.nio.ByteBuffer;
import java.util.List;

public class StringDecoder extends BaseDecoder<ByteBuffer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuffer buffer, List<Object> out) {
        System.out.println("StringDecoder call");
        // before read
        buffer.flip();
        String str = new String(buffer.array());
        out.add(str.trim());
    }
}

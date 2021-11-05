package main.java.netty_example.handler;

import main.java.netty_example.core.AbstractEncoder;
import main.java.netty_example.core.ChannelHandlerContext;

import java.util.List;

public class StringEncoder extends AbstractEncoder<String> {
    @Override
    protected void encoder(ChannelHandlerContext ctx, String str, List<Object> in) {
        System.out.println("StringEncoder call");
        if (str.length() > 0) {
            in.add(str);
        }
    }
}

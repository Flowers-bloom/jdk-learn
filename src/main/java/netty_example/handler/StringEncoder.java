package main.java.netty_example.handler;

import main.java.netty_example.core.BaseEncoder;
import main.java.netty_example.core.ChannelHandlerContext;

import java.util.List;

public class StringEncoder extends BaseEncoder<String> {
    @Override
    protected void encoder(ChannelHandlerContext ctx, String str, List<Object> in) {
        System.out.println("StringEncoder call");
        if (str.length() > 0) {
            in.add(str);
        }
    }
}

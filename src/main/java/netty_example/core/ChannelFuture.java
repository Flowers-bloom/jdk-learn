package main.java.netty_example.core;

public class ChannelFuture {
    private Channel channel;
    private boolean success;
    private ChannelFutureListener listener;

    public Channel channel() {
        return channel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void addListener(ChannelFutureListener listener) {
        this.listener = listener;
    }

    public void callbackListener() {
        if (listener != null && !success) {
            success = true;
            listener.operationComplete(this);
        }
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

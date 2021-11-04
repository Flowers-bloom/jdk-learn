package main.java.netty_example.core;

public abstract class AbstractChannelInitializer {
    private ChannelPipeline pipeline;

    public AbstractChannelInitializer() {
        pipeline = new ChannelPipeline();
    }

    public abstract void initChannel(ChannelPipeline pipeline);

    public void init() {
        initChannel(pipeline);
    }

    public ChannelPipeline getPipeline() {
        return pipeline;
    }
}

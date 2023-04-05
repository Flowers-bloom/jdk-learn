package main.java.handle_tree.core;

import main.java.handle_tree.request.Request;
import main.java.handle_tree.result.Result;

/**
 * Router
 * 路由器，职责在于将请求路由到正确的处理器上；
 * 路由器也是一种特殊的处理器，它的处理逻辑是确定的，那就是执行路由逻辑；
 */
public abstract class Router implements Handler {

    /**
     * 根据请求获取正确的处理器，否则使用默认处理器处理
     * @param request
     * @return
     */
    public final Result apply(Request request) {
        Handler handler = getHandler(request);
        if (handler != null) {
            return handler.exec(request);
        }
        return defaultHandler.exec(request);
    }

    /**
     * 路由器执行逻辑，不允许重写
     * @param request
     * @return
     */
    @Override
    public final Result exec(Request request) {
        return apply(request);
    }

    private final Handler defaultHandler = Handler.DEFAULT_HANDLER;

    protected abstract Handler getHandler(Request request);
}

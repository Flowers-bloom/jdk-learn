package main.java.handle_tree.core;

import main.java.handle_tree.request.Request;
import main.java.handle_tree.result.Result;

/**
 * Handler
 * 处理器，针对请求入参做实际的业务逻辑处理
 */
public interface Handler {

    Handler DEFAULT_HANDLER = request -> null;

    /**
     * 处理器执行逻辑
     * @param request
     * @return
     */
    Result exec(Request request);
}

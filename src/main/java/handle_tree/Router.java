package main.java.handle_tree;

import java.util.ArrayList;
import java.util.List;

public abstract class Router {

    public final Result apply(Request request) {
        List<Handler> nodeList = registerNode();
        for (Handler handler : nodeList) {
            if (handler.isMatch(request)) {
                return handler.execHandle(request);
            }
        }
        return Handler.DEFAULT_HANDLER.execHandle(request);
    }

    private List<Handler> nodeList = new ArrayList<>();

    protected abstract List<Handler> registerNode();
}

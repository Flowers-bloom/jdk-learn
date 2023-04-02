package main.java.handle_tree;

import java.util.ArrayList;
import java.util.List;

public class AbstractRouter extends Router implements Handler {
    @Override
    public Result execHandle(Request request) {
        return apply(request);
    }

    @Override
    protected List<Handler> registerNode() {
        return new ArrayList<>();
    }
}

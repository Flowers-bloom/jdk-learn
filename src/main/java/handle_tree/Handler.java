package main.java.handle_tree;

public interface Handler {

    Handler DEFAULT_HANDLER = req -> new Result("root default result");

    default Boolean isMatch(Request request) {
        return false;
    }

    Result execHandle(Request request);
}

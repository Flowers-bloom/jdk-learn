package main.java.handle_tree.request;

public class Request {
    public static final int VERSION1 = 1;
    public static final int VERSION2 = 2;

    public static final String T1 = "t1";
    public static final String T2 = "t2";

    private final Integer version;
    private String type;

    public Request(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

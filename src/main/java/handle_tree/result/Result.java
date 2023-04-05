package main.java.handle_tree.result;

public class Result {
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Result{" +
                "info='" + info + '\'' +
                '}';
    }
}

package main.java.handle_tree;

import java.util.ArrayList;
import java.util.List;

public class Tester {
    public static void main(String[] args) {
        Root root = new Root();

        System.out.println("version: 1,  param: s1");
        Request req1 = new Request();
        req1.setVersion(1);
        req1.setParam("s1");
        Result r1 = root.apply(req1);
        System.out.println(r1);

        System.out.println("version: 1,  param: s2");
        Request req11 = new Request();
        req11.setVersion(1);
        req11.setParam("s2");
        Result r11 = root.apply(req11);
        System.out.println(r11);

        System.out.println("version: 2,  param: s1");
        Request req2 = new Request();
        req2.setVersion(2);
        req2.setParam("s1");
        Result r2 = root.apply(req2);
        System.out.println(r2);

        System.out.println("version: 2,  param: s2");
        Request req3 = new Request();
        req3.setVersion(2);
        req3.setParam("s2");
        Result r3 = root.apply(req3);
        System.out.println(r3);

        System.out.println("version: 0,  param: s1");
        Request req4 = new Request();
        req4.setVersion(0);
        req4.setParam("s1");
        Result r4 = root.apply(req4);
        System.out.println(r4);
    }

    public static class Root extends AbstractRouter {

        @Override
        protected List<Handler> registerNode() {
            List<Handler> nodeList = new ArrayList<>();
            nodeList.add(new V2Router());
            nodeList.add(new V1DefaultHandler());
            return nodeList;
        }
    }

    public static class V2Router extends AbstractRouter {
        @Override
        public Boolean isMatch(Request request) {
            return request.getVersion() == 2;
        }

        @Override
        protected List<Handler> registerNode() {
            List<Handler> nodeList = new ArrayList<>();
            nodeList.add(new V2S1Handler());
            nodeList.add(new V2S2Handler());
            return nodeList;
        }
    }

    public static class V1DefaultHandler implements Handler {

        @Override
        public Boolean isMatch(Request request) {
            return request.getVersion() == 1;
        }

        @Override
        public Result execHandle(Request request) {
            System.out.println("V1DefaultHandler exec");
            return new Result("v1 result");
        }
    }

    public static class V2S1Handler implements Handler {

        @Override
        public Boolean isMatch(Request request) {
            return request.getVersion() == 2 && "s1".equals(request.getParam());
        }

        @Override
        public Result execHandle(Request request) {
            System.out.println("V2S1Handler exec");
            return new Result("v2s1 result");
        }
    }

    public static class V2S2Handler implements Handler {

        @Override
        public Boolean isMatch(Request request) {
            return request.getVersion() == 2 && "s2".equals(request.getParam());
        }

        @Override
        public Result execHandle(Request request) {
            System.out.println("V2S2Handler exec");
            return new Result("v2s2 result");
        }
    }
}

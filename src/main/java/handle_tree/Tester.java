package main.java.handle_tree;

import main.java.handle_tree.core.Handler;
import main.java.handle_tree.core.Router;
import main.java.handle_tree.request.Request;
import main.java.handle_tree.request.v1.V1Request;
import main.java.handle_tree.request.v2.V2Request;
import main.java.handle_tree.result.Result;
import main.java.handle_tree.result.v1.V1Result;
import main.java.handle_tree.result.v2.V2Result;

public class Tester {
    public static void main(String[] args) {
        Root root = new Root();
        System.out.println("v1 t1");
        V1Request v1Request = new V1Request();
        v1Request.setType(Request.T1);
        Result v1Result = root.apply(v1Request);
        System.out.println(v1Result);

        System.out.println("v1 t2");
        V1Request v1Request2 = new V1Request();
        v1Request2.setType(Request.T2);
        Result v1Result2 = root.apply(v1Request2);
        System.out.println(v1Result2);

        System.out.println("v2 t1");
        V2Request v2Request = new V2Request();
        v2Request.setType(Request.T1);
        Result v2Result = root.apply(v2Request);
        System.out.println(v2Result);

        System.out.println("v2 t2");
        V2Request v2Request2 = new V2Request();
        v2Request2.setType(Request.T2);
        Result v2Result2 = root.apply(v2Request2);
        System.out.println(v2Result2);
    }

    public static class Root extends Router {
        @Override
        protected Handler getHandler(Request request) {
            if (Request.VERSION1 == request.getVersion() && Request.T1.equals(request.getType())) {
                return new V1Handler();
            }else if (Request.VERSION2 == request.getVersion()) {
                return new V2Router();
            }
            return null;
        }
    }

    public static class V2Router extends Router {

        @Override
        protected Handler getHandler(Request request) {
            if (request.getVersion() != Request.VERSION2) {
                return null;
            }
            if (Request.T1.equals(request.getType())) {
                return new V2T1Handler();
            }else if (Request.T2.equals(request.getType())) {
                return new V2T2Handler();
            }
            return null;
        }
    }

    public static class V1Handler implements Handler {

        @Override
        public Result exec(Request request) {
            V1Result result = new V1Result();
            result.setInfo("V1Handler");
            return result;
        }
    }

    public static class V2T1Handler implements Handler {

        @Override
        public Result exec(Request request) {
            V2Result result = new V2Result();
            result.setInfo("V2T1Handler");
            return result;
        }
    }

    public static class V2T2Handler implements Handler {

        @Override
        public Result exec(Request request) {
            V2Result result = new V2Result();
            result.setInfo("V2T1Handler");
            return result;
        }
    }
}

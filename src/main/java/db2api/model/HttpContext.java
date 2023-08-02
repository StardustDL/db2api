package db2api.model;

import db2api.Utils;

public class HttpContext {
    public static class Request {
        public String url;

        public String body;

        public String token;

        public String ToJSON() {
            return Utils.toJSON(this);
        }
    }

    public static class Response {
        public int status;

        public String message;

        public Object data;

        public String ToJSON() {
            return Utils.toJSON(this);
        }

    }

    public Request request = new Request();

    public Response response = new Response();
}

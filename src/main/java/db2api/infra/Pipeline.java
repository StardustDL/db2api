package db2api.infra;

import db2api.model.HttpContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class Pipeline {
    @FunctionalInterface
    public interface Middleware {
        boolean resolve(HttpContext context, ObjectContainer data);
    }

    @FunctionalInterface
    public interface Handler {
        void resolve(HttpContext context, ObjectContainer data);
    }

    public static class ObjectContainer {
        HashMap<String, Object> objs = new HashMap<>();

        public void push(String key, Object obj) {
            objs.put(key, obj);
        }

        public <T> T get(String key) {
            return (T)objs.get(key);
        }
    }

    public ArrayList<Middleware> middlewares = new ArrayList<>();

    public Handler handler = (context, data) -> {};

    public ObjectContainer data = new ObjectContainer();

    public HttpContext.Response resolve(HttpContext.Request request) {
        var context = new HttpContext();
        context.request = request;
        for(var middleware : middlewares) {
            try {
                if (middleware.resolve(context, data)) {
                    return context.response;
                }
            } catch (Exception ex) {
                context.response.status = 500;
                context.response.message = "Error when processing middleware: " + ex.getMessage();
                return context.response;
            }
        }
        try {
            handler.resolve(context, data);
        }
        catch (Exception ex) {
            context.response.status = 500;
            context.response.message = "Error when processing handler: " + ex.getMessage();
        }
        return context.response;
    }
}

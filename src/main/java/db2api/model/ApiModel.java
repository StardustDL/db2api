package db2api.model;

import db2api.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiModel {
    public String name;

    public User user;

    public String ToJSON() {
        return Utils.toJSON(this);
    }

    public static class Condition {
        public String raw;
    }

    public static class Value {
        public HashMap<String, String> attrs = new HashMap<>();
    }

    public static class DeleteRequest extends ApiModel {

        public Condition condition;
    }

    public static class InsertRequest extends ApiModel {
        public ArrayList<Value> values = new ArrayList<>();
    }

    public static class QueryRequest extends ApiModel {

        public static class QueryColumn {
            public String name;

            public String func;
        }

        public static class Pagination {
            public int pageSize;

            public int pageNumber;
        }

        public ArrayList<db2api.model.ApiModel.QueryRequest.QueryColumn> cols;

        public Condition condition;

        public HashMap<String, Boolean> sortAsc;

        public Pagination page;

        public String group;
    }

    public static class JoinQueryRequest extends QueryRequest {
        public String joinTable;

        public Condition joinCond;
    }

    public static class UpdateRequest extends ApiModel {
        public Condition condition;

        public Value value;
    }


}

package db2api.parser.api;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import db2api.model.ApiModel;
import db2api.model.User;
import db2api.parser.ModelJsonParser;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseJsonApiParser extends ModelJsonParser<ApiModel> {
    @Override
    public ApiModel parseJSON(JSONObject json) {

        ApiModel res = new ApiModel();
        String method = json.getStr("method");
        if (method.equals("query")) {
            res = query(json);
        } else if (method.equals("insert")) {
            res = insert(json);
        } else if (method.equals("delete")) {
            res = delete(json);
        } else if (method.equals("update")) {
            res = update(json);
        }
        res.name = json.getStr("name");
        res.user = user(json.getJSONObject("user"));
        return res;
    }

    User user(JSONObject user) {
        User res = new User();
        res.userName = user.getStr("name");
        res.userPassword = user.getStr("password");
        return res;
    }

    db2api.model.ApiModel.Value value(JSONObject json) {
        var res = new db2api.model.ApiModel.Value();
        for(var key : json.keySet()) {
            res.attrs.put(key, json.getStr(key));
        }
        return res;
    }

    ArrayList<db2api.model.ApiModel.Value> values(JSONArray json) {
        var res = new ArrayList<db2api.model.ApiModel.Value>();
        var sz = json.size();
        for (int i=0; i<sz; i++) {
            res.add(value(json.getJSONObject(i)));
        }
        return res;
    }

    db2api.model.ApiModel.Condition cond(JSONObject json) {
        var res = new db2api.model.ApiModel.Condition();
        res.raw = json.getStr("cond");
        return res;
    }

    db2api.model.ApiModel.Condition joinCond(JSONObject json) {
        var res = new db2api.model.ApiModel.Condition();
        res.raw = json.getStr("joinCond");
        return res;
    }

    db2api.model.ApiModel.InsertRequest insert(JSONObject json) {
        var res = new db2api.model.ApiModel.InsertRequest();
        res.values = values(json.getJSONArray("value"));
        return res;
    }

    db2api.model.ApiModel.DeleteRequest delete(JSONObject json) {
        var res = new db2api.model.ApiModel.DeleteRequest();
        res.condition = cond(json);
        return res;
    }

    db2api.model.ApiModel.UpdateRequest update(JSONObject json) {
        var res = new db2api.model.ApiModel.UpdateRequest();
        res.condition = cond(json);
        res.value = value(json.getJSONObject("value"));
        return res;
    }

    HashMap<String, Boolean> sort(String sstr) {
        var sort = new HashMap<String, Boolean>();
        for(var item : sstr.split(",")){
            var s = item.trim();
            var name = s.substring(0, s.length()-1);
            if (s.endsWith("+")){
                sort.put(name, true);
            }
            else if (s.endsWith("-")){
                sort.put(name, true);
            }
        }
        return sort;
    }

    db2api.model.ApiModel.QueryRequest.Pagination page(String sstr) {
        var res = new db2api.model.ApiModel.QueryRequest.Pagination();
        String[] args = sstr.split("@");
        res.pageNumber = Integer.parseInt(args[0]);
        res.pageSize = Integer.parseInt(args[1]);
        return res;
    }

    ArrayList<db2api.model.ApiModel.QueryRequest.QueryColumn> queryCols(JSONArray json) {
        var res = new ArrayList<ApiModel.QueryRequest.QueryColumn>();
        for(var str : json) {
            var s = str.toString();
            var q = new ApiModel.QueryRequest.QueryColumn();
            if (s.contains(":")) {
                String[] args = s.split(":");
                q.func = args[0];
                q.name = args[1];
            } else {
                q.name = s;
                res.add(q);
            }
        }
        return res;
    }

    db2api.model.ApiModel.QueryRequest.QueryRequest query(JSONObject json) {
        db2api.model.ApiModel.QueryRequest.QueryRequest res;
        if (json.containsKey("joinTable")) {
            var cres = new db2api.model.ApiModel.QueryRequest.JoinQueryRequest();
            cres.joinTable = json.getStr("joinTable");
            cres.joinCond = joinCond(json);
            res = cres;
        } else {
            res = new db2api.model.ApiModel.QueryRequest();
        }
        res.condition = cond(json);
        if (json.containsKey("sort")) {
            res.sortAsc = sort(json.getStr("sort"));
        }
        if (json.containsKey("page")) {
            res.page = page(json.getStr("page"));
        }
        if (json.containsKey("group")) {
            res.group = json.getStr("group");
        }
        res.cols = queryCols(json.getJSONArray("cols"));
        return res;
    }
}

package db2api.checker;

import db2api.model.ApiModel;

import db2api.model.ConfigModel;

import java.util.ArrayList;

public class BaseAccessChecker extends AccessChecker {
    @Override
    public ArrayList<String> check(ApiModel request, ConfigModel config) {

        ArrayList<String> err = new ArrayList<>();
        var all_user_config = config.USER_CONFIG_MAP;

        var username = request.user.userName;
        var password = request.user.userPassword;

        System.out.printf("username: %s, password: %s", username, password);
        
        if (!all_user_config.containsKey(username)) {
            err.add(String.format("User %s not found!", username));
            return err;
        }

        if (!all_user_config.get(username).getPassword().equals(password)) {
            err.add("Wrong Password!");
            return err;
        }

        var user_access_map = all_user_config.get(username).USER_CONFIG_MAP;
        var table_name = request.name;
        if (!user_access_map.containsKey(table_name)) {
            err.add(String.format("Table %s not found!", table_name));
            return err;
        }
        var access = user_access_map.get(table_name);
        if (request instanceof DeleteRequest) {
            if (!access.getDelete()) {
                err.add(String.format("No delete access to table %s", table_name));
                return err;
            }
        }
        if (request instanceof ApiModel.InsertRequest) {
            if (!access.getAdd()) {
                err.add(String.format("No insert access to table %s", table_name));
                return err;
            }
        }
        if (request instanceof ApiModel.QueryRequest) {
            if (!access.getGet()) {
                err.add(String.format("No query access to table %s", table_name));
                return err;
            }
        }
        if (request instanceof ApiModel.JoinQueryRequest) {
            var joinReq = (ApiModel.JoinQueryRequest)request;
            var join_table_name = joinReq.joinTable;
            if (!user_access_map.containsKey(join_table_name)) {
                err.add(String.format("Join table %s not found!", join_table_name));
                return err;
            }
            if (!user_access_map.get(join_table_name).getGet()) {
                err.add(String.format("No query access to table %s", table_name));
                return err;
            }
        }
        if (request instanceof ApiModel.UpdateRequest) {
            if (!access.getUpdate()) {
                err.add(String.format("No update access to table %s", table_name));
                return err;
            }
        }
        return err;
    }
}

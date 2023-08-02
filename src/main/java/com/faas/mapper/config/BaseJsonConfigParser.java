package com.faas.mapper.config;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.faas.mapper.ModelJsonParser;
import com.faas.model.ConfigModel;
import com.faas.model.ConfigModel.OperationConfig;
import com.faas.model.ConfigModel.UserConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseJsonConfigParser extends ModelJsonParser<ConfigModel> {

    @Override
    public ConfigModel parseJSON(JSONObject jsonObject) {
        // new一个configmodel
        ConfigModel config_model = new ConfigModel();

        // 提取OperationConfig
        JSONArray arrayOperation = jsonObject.getJSONArray("OperationConfig");
        Map<String, OperationConfig> operationmap = new HashMap<>(arrayOperation.size());
        for (int i = 0; i < arrayOperation.size(); i++) {
            OperationConfig tmpopconfig = config_model.new OperationConfig();
            JSONObject obj = arrayOperation.getJSONObject(i);
            String table_name = (String) obj.get("table_name");
            Boolean add = (Boolean) obj.get("add");
            Boolean delete = (Boolean) obj.get("delete");
            Boolean update = (Boolean) obj.get("update");
            Boolean get = (Boolean) obj.get("get");
            Boolean as = (Boolean) obj.get("as");
            tmpopconfig.setTableName(table_name);
            tmpopconfig.setAdd(add);
            tmpopconfig.setDelete(delete);
            tmpopconfig.setUpdate(update);
            tmpopconfig.setGet(get);
            tmpopconfig.setAs(as);
            operationmap.put(table_name, tmpopconfig);
        }
        config_model.OPERATION_CONFIG_MAP = operationmap;

        // 提取UserConfig
        JSONArray arrayUser = jsonObject.getJSONArray("UserConfig");
        Map<String, UserConfig> usermap = new HashMap<>(arrayUser.size());
        // 根据user数量
        ArrayList<String> err = new ArrayList<>();
        for (int i = 0; i < arrayUser.size(); i++) {
            UserConfig tmpuserconfig = config_model.new UserConfig();
            JSONObject obj = arrayUser.getJSONObject(i);
            String name = (String) obj.get("name");
            String password = (String) obj.get("password");
            tmpuserconfig.setName(name);
            tmpuserconfig.setPassword(password);
            // 提取每个user的OperationConfig
            JSONArray opconfig = obj.getJSONArray("OperationConfig");
            Map<String, OperationConfig> map = new HashMap<>(opconfig.size());
            for (int j = 0; j < opconfig.size(); j++) {
                JSONObject tmpobj = opconfig.getJSONObject(j);
                String table_name = (String) tmpobj.get("table_name");
                Boolean add = (Boolean) tmpobj.get("add");
                Boolean delete = (Boolean) tmpobj.get("delete");
                Boolean update = (Boolean) tmpobj.get("update");
                Boolean get = (Boolean) tmpobj.get("get");
                Boolean as = (Boolean) tmpobj.get("as");

                // 检查user的权限
                // 1. check table_name
                if (!operationmap.containsKey(table_name)) {
                    err.add(String.format("%s Table %s is not found!", name, table_name));
                    System.out.printf("CONFIG_JSON:%s Table %s is not found!\n", name, table_name);
                    return null;
                }
                // 2. check add
                if (add && !operationmap.get(table_name).getAdd()) {
                    err.add(String.format("%s No add access to table %s", name, table_name));
                    System.out.printf("CONFIG_JSON:%s No add access to table %s\n", name, table_name);
                    return null;
                }
                // 3. check delete
                if (delete && !operationmap.get(table_name).getDelete()) {
                    err.add(String.format("%s No delete access to table %s", name, table_name));
                    System.out.printf("CONFIG_JSON:%s No delete access to table %s\n", name, table_name);
                    return null;
                }
                // 4. check update
                if (update && !operationmap.get(table_name).getUpdate()) {
                    err.add(String.format("No update access to table %s", name, table_name));
                    System.out.printf("CONFIG_JSON:No update access to table %s\n", name, table_name);
                    return null;
                }
                // 5. check get
                if (get && !operationmap.get(table_name).getGet()) {
                    err.add(String.format("%s No get access to table %s", name, table_name));
                    System.out.printf("CONFIG_JSON:%s No get access to table %s\n", name, table_name);
                    return null;
                }
                // 6. check as
                if (get && !operationmap.get(table_name).getAs()) {
                    err.add(String.format("%s No as access to table %s", name, table_name));
                    System.out.printf("CONFIG_JSON:%s No as access to table %s\n", name, table_name);
                    return null;
                }
                OperationConfig tmpopconfig = config_model.new OperationConfig();
                tmpopconfig.setTableName(table_name);
                tmpopconfig.setAdd(add);
                tmpopconfig.setDelete(delete);
                tmpopconfig.setUpdate(update);
                tmpopconfig.setGet(get);
                tmpopconfig.setAs(as);
                map.put(table_name, tmpopconfig);
            }
            // 把每个OperationConfig塞入对应的User里面
            tmpuserconfig.setOperationConfig(map);
            usermap.put(name, tmpuserconfig);
        }
        // 赋值给config_model
        config_model.USER_CONFIG_MAP = usermap;

        return config_model;
    }
}

package com.faas.model;

import com.faas.Utils;

import java.util.Map;

public class ConfigModel {
    public Map<String, OperationConfig> OPERATION_CONFIG_MAP;
    public Map<String, UserConfig> USER_CONFIG_MAP;

    public class OperationConfig {
        String table_name;
        Boolean add;
        Boolean delete;
        Boolean update;
        Boolean get;
        Boolean as;

        public String getTableName() {
            return table_name;
        }

        public Boolean getAdd() {
            return add;
        }

        public Boolean getDelete() {
            return delete;
        }

        public Boolean getUpdate() {
            return update;
        }

        public Boolean getGet() {
            return get;
        }

        public Boolean getAs() {
            return as;
        }

        public void setTableName(final String table_name) {
            this.table_name = table_name;
        }

        public void setAdd(final Boolean add) {
            this.add = add;
        }

        public void setDelete(final Boolean delete) {
            this.delete = delete;
        }

        public void setUpdate(final Boolean update) {
            this.update = update;
        }

        public void setGet(final Boolean get) {
            this.get = get;
        }

        public void setAs(final Boolean as) {
            this.as = as;
        }
    }

    public class UserConfig {
        String name;
        String password;
        public Map<String, OperationConfig> USER_CONFIG_MAP;

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setPassword(final String password) {
            this.password = password;
        }

        public void setOperationConfig(final Map<String, OperationConfig> map) {
            this.USER_CONFIG_MAP = map;
        }
    }

    public String ToJSON() {
        return Utils.toJSON(this);
    }
}
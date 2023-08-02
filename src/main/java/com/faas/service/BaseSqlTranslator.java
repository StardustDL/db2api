package com.faas.service;

import com.faas.model.ApiModel;
import com.faas.model.ConfigModel;
import com.faas.model.SqlStatements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BaseSqlTranslator extends SqlTranslator {
    @Override
    public SqlStatements translate(ApiModel request, ConfigModel config)
    {
        ArrayList<String> sqls = new ArrayList<>();

        if (request instanceof ApiModel.InsertRequest) {
            var insertRequest = (ApiModel.InsertRequest) request;
            sqls.add(insertOperation(insertRequest.name, insertRequest.values));
        } else if(request instanceof ApiModel.DeleteRequest) {
            var deleteRequest = (ApiModel.DeleteRequest) request;
            sqls.add(deleteOperation(deleteRequest.name, deleteRequest.condition));
        }

        //sqls.add("select * from test_table_txx where `name`='张三'");

        SqlStatements result = new SqlStatements();
        result.setStatements(sqls);
        return result;
    }

    public String insertOperation(String tableName, ArrayList<ApiModel.Value> rawValues){
        StringBuilder columns = new StringBuilder();
        StringBuilder valueText = new StringBuilder();
        for(var value : rawValues){
            StringBuilder values = new StringBuilder();
            HashMap<String, String> map = value.attrs;
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (valueText.length() == 0) {
                    columns.append(key).append(", ");
                }
                values.append("'").append(map.get(key)).append("', ");
            }
            String valuesRes = values.substring(0, values.length() - 1);
            valueText.append("(" + valuesRes + "),");
        }

        String column = columns.substring(0, columns.length() - 1);

        String res="INSERT INTO "+tableName+" ("+ column+") "+"VALUES "+
                valueText.substring(0, valueText.length() - 1) + ";";
        return res;
    }

    public String deleteOperation(String tableName, ApiModel.Condition condition){

        String res = "DELETE FROM " + tableName + "WHERE " + condition.raw + ";";
        return res;
    }

    public String updateOperation(String tableName, ApiModel.Value value, ApiModel.Condition condition){


        String res = "UPDATE " + tableName + "SET "+ "WHERE " + condition.raw + ";";
        return res;
    }

}

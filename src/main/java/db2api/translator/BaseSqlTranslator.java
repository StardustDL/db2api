package db2api.translator;

import db2api.model.ApiModel;
import db2api.model.ConfigModel;
import db2api.model.SqlStatements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class BaseSqlTranslator extends SqlTranslator {
    @Override
    public SqlStatements translate(ApiModel request, ConfigModel config) {
        ArrayList<String> sqls = new ArrayList<>();

        if (request instanceof ApiModel.InsertRequest) {
            var insertRequest = (ApiModel.InsertRequest) request;
            sqls.add(insertOperation(insertRequest));
        } else if (request instanceof ApiModel.DeleteRequest) {
            var deleteRequest = (ApiModel.DeleteRequest) request;
            sqls.add(deleteOperation(deleteRequest));
        } else if (request instanceof ApiModel.UpdateRequest) {
            var updateRequest = (ApiModel.UpdateRequest) request;
            sqls.add(updateOperation(updateRequest));
        } else if (request instanceof ApiModel.JoinQueryRequest) {
            var joinQueryRequest = (ApiModel.JoinQueryRequest) request;
            sqls.add(joinQueryOperation(joinQueryRequest));
        } else if (request instanceof ApiModel.QueryRequest) {
            var queryRequest = (ApiModel.QueryRequest) request;
            sqls.add(queryOperation(queryRequest));
        }

        SqlStatements result = new SqlStatements();
        result.setStatements(sqls);
        return result;
    }

    public String insertOperation(ApiModel.InsertRequest insertRequest) {
        String tableName = insertRequest.name;
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        HashMap<String, String> map = insertRequest.values.get(0).attrs;
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            columns.append(key).append(", ");
            values.append("'").append(map.get(key)).append("', ");
        }
        String column = columns.substring(0, columns.length() - 1);
        String valuesRes = values.substring(0, values.length() - 1);

        String sql = "INSERT INTO " + tableName + " (" + column + ") " + "VALUES " +
                "(" + valuesRes + ");";
        return sql;
    }

    public String deleteOperation(ApiModel.DeleteRequest deleteRequest) {
        String tableName = deleteRequest.name;

        String sql = "DELETE FROM " + tableName + "WHERE " + deleteRequest.condition.raw + ";";
        return sql;
    }

    public String updateOperation(ApiModel.UpdateRequest updateRequest) {
        String tableName = updateRequest.name;
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        Map<String, String> map = updateRequest.value.attrs;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String column = entry.getKey();
            String new_value = entry.getValue();
            sql.append(column).append(" = '").append(new_value).append("', ");
        }
        // Remove the last comma and space
        sql.setLength(sql.length() - 2);
        return sql.toString();
    }

    public String queryOperation(ApiModel.QueryRequest queryRequest) {
        StringBuilder column = new StringBuilder();
        // 包含聚合查询
        for (ApiModel.QueryRequest.QueryColumn queryColumn : queryRequest.cols) {
            if (queryColumn.func == null) {
                column.append(queryColumn.name);
            } else {
                column.append(queryColumn.func).append("(").append(queryColumn.name).append(")");
            }
            column.append(", ");
        }
        // 删除最后一个逗号
        column.deleteCharAt(column.length() - 2);
        String tableName = queryRequest.name;

        StringBuilder sql = new StringBuilder("select ");
        sql.append(column).append("from ").append(tableName).append(" where ").append(queryRequest.condition.raw);

        // 是否分组
        if (queryRequest.group != null && !queryRequest.group.isEmpty()) {
            sql.append(" GROUP BY ").append(queryRequest.group);
        }

        // 是否排序
        if (queryRequest.sortAsc != null) {
            sql.append(" ORDER BY ");
            HashMap<String, Boolean> map = queryRequest.sortAsc;
            for (Map.Entry<String, Boolean> entry : map.entrySet()) {
                String columnG = entry.getKey();
                Boolean sort = entry.getValue();
                if (sort)
                    sql.append(columnG).append(", ");
                else
                    sql.append(columnG).append(" DESC, ");
            }
            // 删除最后一个逗号
            sql.deleteCharAt(sql.length() - 2);
        }

        // 是否分页查询
        if (queryRequest.page != null) {
            int pageSize = queryRequest.page.pageSize;
            int pageNumber = queryRequest.page.pageNumber;
            // (curPage-1)*pageSize,pageSize
            sql.append("LIMIT ").append((pageNumber - 1) * pageSize).append(", ").append(pageSize);
        }

        return sql.toString();
        // 分组查询，分页查询，聚合查询，排序
    }

    public String joinQueryOperation(ApiModel.JoinQueryRequest queryRequest) {
        String table1 = queryRequest.name;
        String table2 = queryRequest.joinTable;
        StringBuilder column = new StringBuilder();
        // 包含聚合查询
        for (ApiModel.QueryRequest.QueryColumn queryColumn : queryRequest.cols) {
            if (queryColumn.func == null) {
                column.append(queryColumn.name);
            } else {
                column.append(queryColumn.func).append("(").append(queryColumn.name).append(")");
            }
            column.append(", ");
        }
        // 删除最后一个逗号
        column.deleteCharAt(column.length() - 2);

        StringBuilder sql = new StringBuilder("select ").append(column).append("from ").append(table1).append(", ")
                .append(table2).append(" where ").append(queryRequest.condition.raw);
        return sql.toString();
    }
}

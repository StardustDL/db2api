package db2api;

import java.sql.*;

class DbSolver {
    final String CreateSQL = "CREATE TABLE 'test_table' (\n" +
            "'id' BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "'name' VARCHAR(64) DEFAULT NULL,\n" +
            "'comment' VARCHAR(64) DEFAULT NULL,\n" +
            "'score' DOUBLE DEFAULT '0',\n" +
            "'create_time' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "'update_time' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            "PRIMARY KEY('id'),\n" +
            "KEY 'idx_name' ('name')\n" +
            ") AUTO_INCREMENT=5;\n" +
            "\n" +
            "INSERT INTO 'test_table' ('id', 'name', 'comment', 'score') VALUES\n" +
            "(1, '张三', '', 10),\n" +
            "(2, '李四', '', 30)\n" +
            "(3, '王五', '', 32)\n" +
            "(4, '王五', '', 15)\n" +
            "\n" +
            "CREATE TABLE 'test_join' (\n" +
            "'id' BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "'name' VARCHAR(64) DEFAULT NULL,\n" +
            "'city' VARCHAR(64) DEFAULT NULL,\n" +
            "'create_time' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
            "'update_time' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
            "PRIMARY KEY('id'),\n" +
            "KEY 'idx_name' ('name')\n" +
            ") AUTO_INCREMENT=7;\n" +
            "\n" +
            "INSERT INTO 'test_join'('id', 'name', 'city') VALUES\n" +
            "(1, '张三', '北京'),\n" +
            "(2, '李四', '上海'),\n" +
            "(3, '王五', '广州'),\n" +
            "(4, '赵六', '深圳'),\n" +
            "(5, '孙七', '深圳'),\n" +
            "(6, '周八', '深圳');";

    public void init() {
        execute(CreateSQL);
    }

    Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "123456");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(String sql) {
        Connection conn = connect();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(String sql) {
        Connection conn = connect();
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);
            stmt.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

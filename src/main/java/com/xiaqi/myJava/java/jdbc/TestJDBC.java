package com.xiaqi.myJava.java.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 用jdbc实现增删改查功能
 */
public class TestJDBC {

    public static void main(String[] args) {
        String bos = "jdbc:mysql://122.112.161.12:3306/local_station?useUnicode=true&characterEncoding=UTF-8";
        String username = "root";
        String password = "!5rtY6LoP^wr";
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(bos, username, password);
            statement = connection.createStatement();
            select(connection);
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
            // 数据库的连接时有限资源，相关操作结束后，养成关闭数据库的好习惯
            // 先关闭Statement
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ee) {
                }
            }
            // 后关闭Connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ee) {
                }
            }
        }
    }

    public static void select(Connection connection) throws SQLException {
        String sql = "SELECT * FROM b_goods_stock WHERE goods_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        // 使用预编译Statement就可以杜绝SQL注入
        ps.setString(1, "6901683821699");
        ResultSet rs = ps.executeQuery();
        // 查不出数据出来
        while (rs.next()) {
            String goodsCount = rs.getString("goods_count");
            System.out.println(goodsCount);
        }
    }

    public static void insert(Statement statement) throws SQLException {
        String sql = "INSERT INTO b_goods_stock VALUES (NULL, NOW(), NOW(), 'jdbc', 'jdbc', '000000', 1, '0')";
        statement.execute(sql);
    }

    public static void update(Statement statement) throws SQLException {
        String sql = "UPDATE b_goods_stock SET mtime=NOW(), muser='xiaqi', goods_count=0 WHERE goods_id='000000'";
        statement.execute(sql);
    }

    public static void delete(Statement statement) throws SQLException {
        String sql = "DELETE FROM b_goods_stock WHERE goods_id='000000'";
        statement.execute(sql);
    }

}

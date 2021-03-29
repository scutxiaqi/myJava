package com.xiaqi.myJava.java.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOImpl implements DAO {
    public DAOImpl() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://122.112.161.12:3306/local_station?useUnicode=true&characterEncoding=UTF-8", "root", "!5rtY6LoP^wr");
    }

    @Override
    public void insert(BGoodsStock item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(BGoodsStock item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteById(int pkno) {
        // TODO Auto-generated method stub

    }

    @Override
    public BGoodsStock selectOne(String goodsId) {
        // TODO Auto-generated method stub
        return null;
    }

}

package com.xiaqi.myJava.java.jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自己构建的数据库连接池，不是很完善和健壮，用来帮自己理解市面上商业化连接池的基本原理。<br>
 * 1. ConnectionPool()构造方法约定了这个连接池一共有多少连接。<br>
 * 2. 在init()初始化方法中，创建了size条连接。 <br>
 * 3. getConnection， 判断是否为空，如果是空的就wait等待，否则就借用一条连接出去。<br>
 * 4. returnConnection， 在使用完毕后，归还这个连接到连接池，并且在归还完毕后，调用notifyAll，通知那些等待的线程，有新的连接可以借用了。<br>
 */
public class MyConnectionPool {
    List<Connection> cs = new ArrayList<Connection>();

    int size;

    public MyConnectionPool(int size) {
        this.size = size;
        init();
    }

    public void init() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            for (int i = 0; i < size; i++) {
                Connection c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/how2java?characterEncoding=UTF-8", "root", "admin");
                cs.add(c);
            }
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public synchronized Connection getConnection() {
        while (cs.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        Connection c = cs.remove(0);
        return c;
    }

    public synchronized void returnConnection(Connection c) {
        cs.add(c);
        this.notifyAll();
    }
}

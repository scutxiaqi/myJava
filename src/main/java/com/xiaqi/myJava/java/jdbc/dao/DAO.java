package com.xiaqi.myJava.java.jdbc.dao;

public interface DAO {
    public void insert(BGoodsStock item);
    
    public void update(BGoodsStock item);
    
    public void deleteById(int pkno);
    
    public BGoodsStock selectOne(String goodsId);
}

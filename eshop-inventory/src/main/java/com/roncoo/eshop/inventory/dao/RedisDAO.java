package com.roncoo.eshop.inventory.dao;

public interface RedisDAO {

    public void set(String key,String value);

    public String get(String key);

    void delete(String key);
}

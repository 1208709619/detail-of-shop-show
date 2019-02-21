package com.roncoo.eshop.inventory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.UserMapper;
import com.roncoo.eshop.inventory.model.User;
import com.roncoo.eshop.inventory.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisDAO redisDAO;

    @Override
    public User getUserInfo() {
        return userMapper.findUserInfo();
    }

    @Override
    public User getCacheUserInfo() {
        redisDAO.set("cached_user", "{\"name\": \"zhangsan\", \"age\": 25}") ;
        String json = redisDAO.get("cached_user");
        JSONObject jsonObject = JSONObject.parseObject(json);

        User user = new User();
        user.setName(jsonObject.getString("name"));
        user.setAge(jsonObject.getInteger("age"));

        return user;
    }


}

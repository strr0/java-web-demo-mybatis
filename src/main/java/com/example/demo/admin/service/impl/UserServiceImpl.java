package com.example.demo.admin.service.impl;

import com.example.demo.admin.mapper.UserMapper;
import com.example.demo.admin.model.User;
import com.example.demo.admin.service.UserService;
import com.example.demo.config.mybatis.mapper.CrudMapper;
import com.example.demo.config.mybatis.service.impl.CrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends CrudServiceImpl<User, Integer> implements UserService {
    private final UserMapper userMapper;

    @Override
    protected CrudMapper<User, Integer> getMapper() {
        return userMapper;
    }

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}

package com.example.demo.admin.mapper;

import com.example.demo.admin.model.User;
import com.example.demo.config.mybatis.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends CrudMapper<User, Integer> {
}

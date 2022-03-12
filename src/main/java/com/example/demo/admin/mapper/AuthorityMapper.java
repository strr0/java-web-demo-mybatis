package com.example.demo.admin.mapper;

import com.example.demo.admin.model.Authority;
import com.example.demo.config.mybatis.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthorityMapper extends CrudMapper<Authority, Integer> {
    List<Authority> list();
}

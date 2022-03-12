package com.example.demo.admin.service.impl;

import com.example.demo.admin.mapper.AuthorityMapper;
import com.example.demo.admin.model.Authority;
import com.example.demo.admin.service.AuthorityService;
import com.example.demo.config.mybatis.mapper.CrudMapper;
import com.example.demo.config.mybatis.service.impl.CrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl extends CrudServiceImpl<Authority, Integer> implements AuthorityService {
    private final AuthorityMapper authorityMapper;

    @Override
    protected CrudMapper<Authority, Integer> getMapper() {
        return authorityMapper;
    }

    @Autowired
    public AuthorityServiceImpl(AuthorityMapper authorityMapper) {
        this.authorityMapper = authorityMapper;
    }
}

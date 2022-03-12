package com.example.demo.admin.controller;

import com.example.demo.admin.model.Authority;
import com.example.demo.admin.service.AuthorityService;
import com.example.demo.config.mybatis.controller.CrudController;
import com.example.demo.config.mybatis.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/authority")
public class AuthorityController extends CrudController<Authority, Integer> {
    private final AuthorityService authorityService;

    @Override
    protected CrudService<Authority, Integer> getService() {
        return authorityService;
    }

    @Autowired
    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }
}

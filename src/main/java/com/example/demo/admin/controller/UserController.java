package com.example.demo.admin.controller;

import com.example.demo.admin.model.User;
import com.example.demo.admin.service.UserService;
import com.example.demo.config.mybatis.controller.CrudController;
import com.example.demo.config.mybatis.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class UserController extends CrudController<User, Integer> {
    private final UserService userService;

    @Override
    protected CrudService<User, Integer> getService() {
        return userService;
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
}

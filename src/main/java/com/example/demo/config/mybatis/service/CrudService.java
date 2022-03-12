package com.example.demo.config.mybatis.service;

import com.example.demo.config.mybatis.model.Page;
import com.example.demo.config.mybatis.model.Pageable;

import java.io.Serializable;
import java.util.List;

public interface CrudService <T, ID extends Serializable> {
    Page<T> page(T param, Pageable pageable);
    int save(T entity);
    int update(T entity);
    int remove(ID id);
    T get(ID id);
}
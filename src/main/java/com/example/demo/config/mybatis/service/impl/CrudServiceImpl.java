package com.example.demo.config.mybatis.service.impl;

import com.example.demo.config.mybatis.mapper.CrudMapper;
import com.example.demo.config.mybatis.model.Page;
import com.example.demo.config.mybatis.model.Pageable;
import com.example.demo.config.mybatis.service.CrudService;
import com.example.demo.config.mybatis.util.PageUtil;

import java.io.Serializable;
import java.util.List;

public abstract class CrudServiceImpl<T, ID extends Serializable> implements CrudService<T, ID> {
    protected abstract CrudMapper<T, ID> getMapper();

    @Override
    public Page<T> page(T param, Pageable pageable) {
        Page<T> page = pageable.toPage();
        int count = getMapper().countByParam(param);
        page.setTotal(count);
        if (count > 0) {
            PageUtil.start(pageable);
            page.setContent(getMapper().listByParam(param));
            PageUtil.end();
        }
        return page;
    }

    @Override
    public int save(T entity) {
        return getMapper().save(entity);
    }

    @Override
    public int update(T entity) {
        return getMapper().update(entity);
    }

    @Override
    public int remove(ID id) {
        return getMapper().remove(id);
    }

    @Override
    public T get(ID id) {
        return getMapper().get(id);
    }
}

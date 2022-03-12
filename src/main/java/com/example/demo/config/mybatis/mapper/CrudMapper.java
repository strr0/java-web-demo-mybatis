package com.example.demo.config.mybatis.mapper;

import java.io.Serializable;
import java.util.List;

public interface CrudMapper<T, ID extends Serializable> {
    int countByParam(T param);
    List<T> listByParam(T param);
    int save(T entity);
    int update(T entity);
    int remove(ID id);
    T get(ID id);
}

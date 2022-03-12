package com.example.demo.config.mybatis.builder;

import com.example.demo.config.mybatis.mapper.CrudMapper;
import com.example.demo.config.mybatis.exception.BuilderException;
import com.example.demo.config.mybatis.util.EntityUtil;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrudMappedStatementBuilder {
    private final Configuration configuration;
    private final CrudSqlSourceBuilder crudSqlSourceBuilder;

    public CrudMappedStatementBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.crudSqlSourceBuilder = new CrudSqlSourceBuilder(configuration);
    }

    public void addCrudStatement(Class<?> mapper) {
        // 是否实现CrudMapper接口
        if (!CrudMapper.class.isAssignableFrom(mapper)) {
            return;
        }
        // 获取BaseMapper的泛型参数
        Class<?> clazz = (Class<?>) ((ParameterizedType) mapper.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        addCountByParamStatement(clazz, String.format("%s.countByParam", mapper.getTypeName()));
        addListByParamStatement(clazz, String.format("%s.listByParam", mapper.getTypeName()));
        addSaveStatement(clazz, String.format("%s.save", mapper.getTypeName()));
        addUpdateStatement(clazz, String.format("%s.update", mapper.getTypeName()));
        addRemoveStatement(clazz, String.format("%s.remove", mapper.getTypeName()));
        addGetStatement(clazz, String.format("%s.get", mapper.getTypeName()));
    }

    public void addCountByParamStatement(Class<?> clazz, String id) {
        SqlSource countByParam = crudSqlSourceBuilder.countByParamSqlSource(clazz);
        MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(
                configuration,
                id,
                countByParam,
                SqlCommandType.SELECT
        );
        mappedStatementBuilder.resultMaps(simpleResultMaps(int.class));
        configuration.addMappedStatement(mappedStatementBuilder.build());
    }

    public void addListByParamStatement(Class<?> clazz, String id) {
        SqlSource listByParam = crudSqlSourceBuilder.listByParamSqlSource(clazz);
        MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(
                configuration,
                id,
                listByParam,
                SqlCommandType.SELECT
        );
        mappedStatementBuilder.resultMaps(resultMaps(clazz));
        configuration.addMappedStatement(mappedStatementBuilder.build());
    }

    public void addSaveStatement(Class<?> clazz, String id) {
        SqlSource save = crudSqlSourceBuilder.saveSqlSource(clazz);
        MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(
                configuration,
                id,
                save,
                SqlCommandType.INSERT
        );
        mappedStatementBuilder.resultMaps(simpleResultMaps(int.class));
        configuration.addMappedStatement(mappedStatementBuilder.build());
    }

    public void addUpdateStatement(Class<?> clazz, String id) {
        try {
            SqlSource update = crudSqlSourceBuilder.updateSqlSource(clazz);
            MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(
                    configuration,
                    id,
                    update,
                    SqlCommandType.UPDATE
            );
            mappedStatementBuilder.resultMaps(simpleResultMaps(int.class));
            configuration.addMappedStatement(mappedStatementBuilder.build());
        } catch (BuilderException e) {
            e.printStackTrace();
        }
    }

    public void addRemoveStatement(Class<?> clazz, String id) {
        try {
            SqlSource remove = crudSqlSourceBuilder.removeSqlSource(clazz);
            MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(
                    configuration,
                    id,
                    remove,
                    SqlCommandType.DELETE
            );
            mappedStatementBuilder.resultMaps(simpleResultMaps(int.class));
            configuration.addMappedStatement(mappedStatementBuilder.build());
        } catch (BuilderException e) {
            e.printStackTrace();
        }
    }

    public void addGetStatement(Class<?> clazz, String id) {
        try {
            SqlSource get = crudSqlSourceBuilder.getSqlSource(clazz);
            MappedStatement.Builder mappedStatementBuilder = new MappedStatement.Builder(
                    configuration,
                    id,
                    get,
                    SqlCommandType.SELECT
            );
            mappedStatementBuilder.resultMaps(resultMaps(clazz));
            configuration.addMappedStatement(mappedStatementBuilder.build());
        } catch (BuilderException e) {
            e.printStackTrace();
        }
    }

    /**
     * 简单结果集
     * @param clazz
     * @return
     */
    public List<ResultMap> simpleResultMaps(Class<?> clazz) {
        String entity = clazz.getSimpleName();
        return Collections.singletonList(new ResultMap.Builder(configuration, entity, clazz, new ArrayList<>()).build());
    }

    /**
     * 结果集映射
     * @param clazz
     * @return
     */
    public List<ResultMap> resultMaps(Class<?> clazz) {
        String entity = clazz.getSimpleName();
        List<ResultMapping> resultMappings = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            String property = field.getName();
            String column = EntityUtil.getColumn(field);
            resultMappings.add(new ResultMapping.Builder(configuration, property, column, field.getType()).build());
        }
        return Collections.singletonList(new ResultMap.Builder(configuration, entity, clazz, resultMappings).build());
    }
}

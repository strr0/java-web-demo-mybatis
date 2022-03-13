package com.example.demo.config.mybatis.builder;

import com.example.demo.config.mybatis.exception.BuilderException;
import com.example.demo.config.mybatis.exception.KeyNotFoundException;
import com.example.demo.config.mybatis.util.EntityUtil;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrudSqlSourceBuilder {
    private final Configuration configuration;

    public CrudSqlSourceBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public SqlSource countByParamSqlSource(Class<?> clazz) {
        String table = EntityUtil.getTable(clazz);
        SqlNode sqlNode = new StaticTextSqlNode(countSql(table).toString());
        return new DynamicSqlSource(configuration, new MixedSqlNode(Arrays.asList(sqlNode, applyWhere(clazz.getDeclaredFields()))));
    }

    public SqlSource listByParamSqlSource(Class<?> clazz) {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(listSql(table, fields).toString());
        return new DynamicSqlSource(configuration, new MixedSqlNode(Arrays.asList(sqlNode, applyWhere(fields))));
    }

    public SqlSource saveSqlSource(Class<?> clazz) {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(saveSql(table, fields).toString());
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    public SqlSource updateSqlSource(Class<?> clazz) throws BuilderException {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(updateSql(table, fields).toString());
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    public SqlSource removeSqlSource(Class<?> clazz) throws BuilderException {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(removeSql(table, fields).toString());
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    public SqlSource getSqlSource(Class<?> clazz) throws BuilderException {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(getSql(table, fields).toString());
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    private SQL countSql(String table) {
        SQL sql = new SQL();
        sql.SELECT("count(1)");
        sql.FROM(table);
        return sql;
    }

    private SQL listSql(String table, Field[] fields) {
        SQL sql = new SQL();
        for (Field field : fields) {
            sql.SELECT(EntityUtil.getColumn(field));
        }
        sql.FROM(table);
        return sql;
    }

    private SQL saveSql(String table, Field[] fields) {
        SQL sql = new SQL();
        sql.INSERT_INTO(table);
        for (Field field : fields) {
            if (EntityUtil.isKey(field)) {
                continue;
            }
            sql.VALUES(EntityUtil.getColumn(field), String.format("#{%s}", field.getName()));
        }
        return sql;
    }

    private SQL updateSql(String table, Field[] fields) throws KeyNotFoundException {
        Field key = EntityUtil.getKey(fields);
        String keyColumn = EntityUtil.getColumn(key);
        String keyProperty = key.getName();
        SQL sql = new SQL();
        sql.UPDATE(table);
        for (Field field : fields) {
            if (EntityUtil.isKey(field)) {
                continue;
            }
            sql.SET(String.format("%s = #{%s}", EntityUtil.getColumn(field), field.getName()));
        }
        sql.WHERE(String.format("%s = #{%s}", keyColumn, keyProperty));
        return sql;
    }

    private SQL removeSql(String table, Field[] fields) throws KeyNotFoundException {
        Field key = EntityUtil.getKey(fields);
        String keyColumn = EntityUtil.getColumn(key);
        String keyProperty = key.getName();
        SQL sql = new SQL();
        sql.DELETE_FROM(table);
        sql.WHERE(String.format("%s = #{%s}", keyColumn, keyProperty));
        return sql;
    }

    private SQL getSql(String table, Field[] fields) throws KeyNotFoundException {
        Field key = EntityUtil.getKey(fields);
        String keyColumn = EntityUtil.getColumn(key);
        String keyProperty = key.getName();
        SQL sql = new SQL();
        for (Field field : fields) {
            sql.SELECT(EntityUtil.getColumn(field));
        }
        sql.FROM(table);
        sql.WHERE(String.format("%s = #{%s}", keyColumn, keyProperty));
        return sql;
    }

    /**
     * 构建where条件
     * @param fields
     * @return
     */
    private SqlNode applyWhere(Field[] fields) {
        List<SqlNode> ifNodes = new ArrayList<>();
        for (Field field : fields) {
            String property = field.getName();
            String column = EntityUtil.getColumn(field);
            ifNodes.add(new IfSqlNode(new TextSqlNode(String.format(" and %s = #{%s} ", column, property)), String.format("%s != null && %s != ''", property, property)));
        }
        return new WhereSqlNode(configuration, new MixedSqlNode(ifNodes));
    }
}

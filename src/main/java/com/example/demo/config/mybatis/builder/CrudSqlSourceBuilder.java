package com.example.demo.config.mybatis.builder;

import com.example.demo.config.mybatis.exception.BuilderException;
import com.example.demo.config.mybatis.util.EntityUtil;
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
        SqlNode sqlNode = new StaticTextSqlNode("select count(1) from " + table);
        return new DynamicSqlSource(configuration, new MixedSqlNode(Arrays.asList(sqlNode, applyWhere(clazz.getDeclaredFields()))));
    }

    public SqlSource listByParamSqlSource(Class<?> clazz) {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(String.format("select %s from %s", buildColumns(fields), table));
        return new DynamicSqlSource(configuration, new MixedSqlNode(Arrays.asList(sqlNode, applyWhere(fields))));
    }

    public SqlSource saveSqlSource(Class<?> clazz) {
        String table = EntityUtil.getTable(clazz);
        Field[] fields = clazz.getDeclaredFields();
        SqlNode sqlNode = new StaticTextSqlNode(String.format("insert into %s(%s) values(%s)", table, buildColumns(fields), buildSaveParam(fields)));
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    public SqlSource updateSqlSource(Class<?> clazz) throws BuilderException {
        Field[] fields = clazz.getDeclaredFields();
        // 无主键
        if (!EntityUtil.hasKey(fields)) {
            throw new BuilderException();
        }
        String table = EntityUtil.getTable(clazz);
        // 主键
        Field key = fields[0];
        String keyProperty = key.getName();
        String keyColumn = EntityUtil.getColumn(key);
        SqlNode sqlNode = new StaticTextSqlNode(String.format("update %s set %s where %s = #{%s}", table, buildUpdateParam(fields), keyColumn, keyProperty));
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    public SqlSource removeSqlSource(Class<?> clazz) throws BuilderException {
        Field[] fields = clazz.getDeclaredFields();
        // 无主键
        if (!EntityUtil.hasKey(fields)) {
            throw new BuilderException();
        }
        String table = EntityUtil.getTable(clazz);
        // 主键
        Field key = fields[0];
        String keyProperty = key.getName();
        String keyColumn = EntityUtil.getColumn(key);
        SqlNode sqlNode = new StaticTextSqlNode(String.format("delete from %s where %s = #{%s}", table, keyColumn, keyProperty));
        return new RawSqlSource(configuration, sqlNode, clazz);
    }

    public SqlSource getSqlSource(Class<?> clazz) throws BuilderException {
        Field[] fields = clazz.getDeclaredFields();
        // 无主键
        if (!EntityUtil.hasKey(fields)) {
            throw new BuilderException();
        }
        String table = EntityUtil.getTable(clazz);
        // 主键
        Field key = fields[0];
        String keyProperty = key.getName();
        String keyColumn = EntityUtil.getColumn(key);
        SqlNode sqlNode = new StaticTextSqlNode(String.format("select %s from %s where %s = #{%s}", buildColumns(fields), table, keyColumn, keyProperty));
        return new RawSqlSource(configuration, sqlNode, clazz);
    }



    /**
     * 构建参数
     * @param fields
     * @return
     */
    private String buildColumns(Field[] fields) {
        List<String> columns = new ArrayList<>();
        for (Field field : fields) {
            // 主键
            if (EntityUtil.isKey(field)) {
                continue;
            }
            String column = EntityUtil.getColumn(field);
            columns.add(column);
        }
        return String.join(", ", columns);
    }

    /**
     * 构建保存参数
     * @param fields
     * @return
     */
    private String buildSaveParam(Field[] fields) {
        List<String> param = new ArrayList<>();
        for (Field field : fields) {
            if (EntityUtil.isKey(field)) {
                continue;
            }
            param.add(String.format("#{%s}", field.getName()));
        }
        return String.join(", ", param);
    }

    /**
     * 构建修改参数
     * @param fields
     * @return
     */
    private String buildUpdateParam(Field[] fields) {
        List<String> param = new ArrayList<>();
        for (Field field : fields) {
            if (EntityUtil.isKey(field)) {
                continue;
            }
            String property = field.getName();
            String column = EntityUtil.getColumn(field);
            param.add(String.format("%s = #{%s}", column, property));
        }
        return String.join(", ", param);
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

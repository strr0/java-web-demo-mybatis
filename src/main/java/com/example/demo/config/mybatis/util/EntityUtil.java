package com.example.demo.config.mybatis.util;

import com.example.demo.config.mybatis.annotation.Column;
import com.example.demo.config.mybatis.annotation.Id;
import com.example.demo.config.mybatis.annotation.Table;

import java.lang.reflect.Field;

public class EntityUtil {
    /**
     * 获取表名
     * @param clazz
     * @return
     */
    public static String getTable(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).value();
        }
        return clazz.getSimpleName();
    }

    /**
     * 获取字段名
     * @param field
     * @return
     */
    public static String getColumn(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).value();
        }
        return field.getName();
    }

    /**
     * 是否主键
     * @param field
     * @return
     */
    public static boolean isKey(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    /**
     * 是否有主键
     * @param fields
     * @return
     */
    public static boolean hasKey(Field[] fields) {
        return fields.length > 0 && fields[0].isAnnotationPresent(Id.class);
    }
}

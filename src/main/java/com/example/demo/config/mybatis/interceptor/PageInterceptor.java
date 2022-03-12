package com.example.demo.config.mybatis.interceptor;

import com.example.demo.config.mybatis.model.Pageable;
import com.example.demo.config.mybatis.util.PageUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * 分页插件
 */
@Component
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class PageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                new DefaultReflectorFactory());
        Pageable pageable = PageUtil.get();
        if (pageable != null) {
            //Connection connection = (Connection) invocation.getArgs()[0];
            String sql = statementHandler.getBoundSql().getSql();
            metaObject.setValue("delegate.boundSql.sql", String.format("%s limit %d, %d", sql, pageable.getPage() * pageable.getSize(), pageable.getSize()));
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}

package com.example.demo.config;

import com.example.demo.config.mybatis.builder.CrudMapperScannerBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@org.springframework.context.annotation.Configuration
@Component
public class MybatisConfig {
    @Autowired
    public MybatisConfig(SqlSessionFactory sqlSessionFactory) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        CrudMapperScannerBuilder builder = new CrudMapperScannerBuilder(configuration);
        builder.scanMapper();
    }
}

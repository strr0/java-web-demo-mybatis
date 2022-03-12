package com.example.demo.config.mybatis.builder;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

public class CrudMapperScannerBuilder {
    private static final String BASE_PACKAGE = "com.example.demo";
    private static final String RESOURCE_PATTERN = "/**/mapper/*.class";

    private final CrudMappedStatementBuilder crudMappedStatementBuilder;

    public CrudMapperScannerBuilder(Configuration configuration) {
        this.crudMappedStatementBuilder = new CrudMappedStatementBuilder(configuration);
    }

    public void scanMapper() {
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                //用于读取类信息
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                //扫描到的class
                String classname = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(classname);
                //判断是否有指定注解
                if (clazz.isAnnotationPresent(Mapper.class)) {
                    //System.out.println(classname);
                    crudMappedStatementBuilder.addCrudStatement(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

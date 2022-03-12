package com.example.demo.config.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Configuration
@Deprecated
public class CrudMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(CrudMapperScannerRegistrar.class);

    private BeanFactory beanFactory;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!AutoConfigurationPackages.has(this.beanFactory)) {
            logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
        } else {
            logger.debug("Searching for mappers annotated with @Mapper");
            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (logger.isDebugEnabled()) {
                packages.forEach((pkg) -> {
                    logger.debug("Using auto-configuration base package '{}'", pkg);
                });
            }

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CrudMapperScannerConfigurer.class);
            builder.addPropertyValue("processPropertyPlaceHolders", true);
            builder.addPropertyValue("annotationClass", Mapper.class);
            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
            BeanWrapper beanWrapper = new BeanWrapperImpl(CrudMapperScannerConfigurer.class);
            Set<String> propertyNames = (Set)Stream.of(beanWrapper.getPropertyDescriptors()).map(FeatureDescriptor::getName).collect(Collectors.toSet());
            if (propertyNames.contains("lazyInitialization")) {
                builder.addPropertyValue("lazyInitialization", "${mybatis.lazy-initialization:false}");
            }

            if (propertyNames.contains("defaultScope")) {
                builder.addPropertyValue("defaultScope", "${mybatis.mapper-default-scope:}");
            }

            registry.registerBeanDefinition(CrudMapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

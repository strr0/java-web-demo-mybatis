package com.example.demo.config.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.aop.scope.ScopedProxyFactoryBean;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * 自定义扫描器
 */
@Deprecated
public class CrudClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrudClassPathMapperScanner.class);

    private final Class<?> mapperFactoryBeanClass = MapperFactoryBean.class;

    public CrudClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    // 过滤条件
    public void registerFilters() {
        super.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
    }

    // 扫描
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            LOGGER.warn(() -> {
                return "No MyBatis mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.";
            });
        } else {
            this.processBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        BeanDefinitionRegistry registry = this.getRegistry();

        for (BeanDefinitionHolder holder : beanDefinitions) {
            AbstractBeanDefinition definition = (AbstractBeanDefinition) holder.getBeanDefinition();
            boolean scopedProxy = false;
            if (ScopedProxyFactoryBean.class.getName().equals(definition.getBeanClassName())) {
                definition = (AbstractBeanDefinition) Optional.ofNullable(((RootBeanDefinition) definition).getDecoratedDefinition()).map(BeanDefinitionHolder::getBeanDefinition).orElseThrow(() -> {
                    return new IllegalStateException("The target bean definition of scoped proxy bean not found. Root bean definition[" + holder + "]");
                });
                scopedProxy = true;
            }

            String beanClassName = definition.getBeanClassName();
            LOGGER.debug(() -> {
                return "Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName + "' mapperInterface";
            });
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.setBeanClass(this.mapperFactoryBeanClass);
            definition.getPropertyValues().add("addToConfig", true);
            definition.setAttribute("factoryBeanObjectType", beanClassName);
            boolean explicitFactoryUsed = false;

            if (!explicitFactoryUsed) {
                LOGGER.debug(() -> {
                    return "Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.";
                });
                definition.setAutowireMode(2);
            }

            definition.setLazyInit(false);
            if (!scopedProxy) {
                if (!definition.isSingleton()) {
                    BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(holder, registry, true);
                    if (registry.containsBeanDefinition(proxyHolder.getBeanName())) {
                        registry.removeBeanDefinition(proxyHolder.getBeanName());
                    }

                    registry.registerBeanDefinition(proxyHolder.getBeanName(), proxyHolder.getBeanDefinition());
                }
            }
        }

    }

    // 验证
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    // 验证
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            LOGGER.warn(() -> {
                return "Skipping MapperFactoryBean with name '" + beanName + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface. Bean already defined with the same name!";
            });
            return false;
        }
    }
}

package org.hejki.spring.data.jdbc.repository.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public class JdbcTemplateFactoryBean extends AbstractFactoryBean<JdbcTemplate> implements ApplicationContextAware {
    private ListableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcTemplate.class;
    }

    @Override
    protected JdbcTemplate createInstance() throws Exception {
        DataSource dataSource = beanFactory.getBean(DataSource.class);
        return new JdbcTemplate(dataSource);
    }
}

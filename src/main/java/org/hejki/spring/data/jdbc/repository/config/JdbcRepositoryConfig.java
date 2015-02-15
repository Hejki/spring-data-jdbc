package org.hejki.spring.data.jdbc.repository.config;

import org.hejki.spring.data.jdbc.BeanPropertyRowUnmapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.lang.annotation.*;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JdbcRepositoryConfig {

    Class<? extends org.springframework.jdbc.core.RowMapper> rowMapper() default BeanPropertyRowMapper.class;

    Class<? extends org.hejki.spring.data.jdbc.RowUnmapper> rowUnmapper() default BeanPropertyRowUnmapper.class;
}

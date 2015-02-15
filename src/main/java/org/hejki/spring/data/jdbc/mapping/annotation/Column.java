package org.hejki.spring.data.jdbc.mapping.annotation;

import java.lang.annotation.*;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * (Optional) The name of the column. Defaults to
     * the property or field name.
     */
    String name() default "";
}

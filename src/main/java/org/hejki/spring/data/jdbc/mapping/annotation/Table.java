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
public @interface Table {
    /**
     * (Optional) The name of the table.
     * <p/>
     * Defaults to the entity name.
     */
    String name() default "";
}

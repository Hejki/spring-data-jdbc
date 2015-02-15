package org.hejki.spring.data.jdbc;

import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public class BeanPropertyRowUnmapper<T> implements RowUnmapper<T> {
    @Override
    public Map<String, Object> mapColumns(final T t) {
        final Map<String, Object> columns = new HashMap<>();

        ReflectionUtils.doWithFields(t.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                columns.put(columnName(field), field.get(t));
            }
        }, persistenceFieldFilter);
        return columns;
    }

    private String columnName(Field field) {
        return field.getName();
    }

    private static ReflectionUtils.FieldFilter persistenceFieldFilter = new ReflectionUtils.FieldFilter() {
        private final String[] excludeNameMatchers = {
                "class",
                "this\\$.*",
                "metaClass"
        };
        private final String[] excludeTypes = {
                "groovy.lang.MetaClass"
        };

        @Override
        public boolean matches(Field field) {
            if (Modifier.isStatic(field.getModifiers())) {
                return false;
            }

            if (Modifier.isTransient(field.getModifiers())) {
                return false;
            }

            for (String exclude : excludeNameMatchers) {
                if (field.getName().matches(exclude)) {
                    return false;
                }
            }

            for (String exclude : excludeTypes) {
                if (field.getType().getName().equals(exclude)) {
                    return false;
                }
            }

            for (Annotation annotation : field.getAnnotations()) {
                if ("Transient".equals(annotation.getClass().getName())) {
                    return false;
                }
            }

            return true;
        }
    };
}
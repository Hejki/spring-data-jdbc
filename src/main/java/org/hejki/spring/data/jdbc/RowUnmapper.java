package org.hejki.spring.data.jdbc;

import java.util.Map;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public interface RowUnmapper<T> {
    Map<String, Object> mapColumns(T t);
}

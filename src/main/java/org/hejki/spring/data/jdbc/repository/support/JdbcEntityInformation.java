package org.hejki.spring.data.jdbc.repository.support;

import org.hejki.spring.data.jdbc.mapping.annotation.Table;
import org.hejki.spring.data.jdbc.repository.config.JdbcRepositoryConfigUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Base class for {@link JdbcEntityInformation} implementations to share common method implementations.
 *
 * @author Hejki
 */
public class JdbcEntityInformation<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {
    private static final Class<Id> ID_ANNOTATION = Id.class;

    private String tableName;
    private Field idField;
    private String idFieldName;

	/**
	 * Creates a new {@link JdbcEntityInformation} with the given domain class.
	 *
	 * @param domainClass must not be {@literal null}.
	 */
	public JdbcEntityInformation(Class<T> domainClass, JdbcRepositoryConfigUtil configUtil) {
		super(domainClass);

        ReflectionUtils.doWithFields(domainClass, new ReflectionUtils.FieldCallback() {
            public void doWith(Field field) {
                if (field.getAnnotation(ID_ANNOTATION) != null) {
                    idField = field;
                    idFieldName = field.getName();
                }
            }
        });
        Assert.notNull(this.idField, String.format("No field annotated with %s found!", ID_ANNOTATION.toString()));
        ReflectionUtils.makeAccessible(idField);

        Table table = AnnotationUtils.getAnnotation(domainClass, Table.class);
        if (null != table && StringUtils.hasText(table.name())) {
            tableName = table.name();
        } else {
            tableName = configUtil.getTableNameResolver().resolve(getJavaType().getSimpleName());
        }
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumn() {
        return idFieldName;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.EntityInformation#getId(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public ID getId(Object entity) {
        return entity == null ? null : (ID) ReflectionUtils.getField(idField, entity);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.EntityInformation#getIdType()
     */
    @SuppressWarnings("unchecked")
    public Class<ID> getIdType() {
        return (Class<ID>) idField.getType();
    }
}
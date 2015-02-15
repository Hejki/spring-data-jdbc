package org.hejki.spring.data.jdbc.repository.config;

import org.hejki.spring.data.jdbc.mapping.NameResolver;
import org.hejki.spring.data.jdbc.repository.sql.SqlGenerator;
import org.springframework.beans.factory.BeanCreationException;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public class JdbcRepositoryConfigUtil {
    private NameResolver tableNameResolver;
    private SqlGenerator sqlGenerator;

    public NameResolver getTableNameResolver() {
        return tableNameResolver;
    }

    public void setTableNameResolverClass(Class<? extends NameResolver> tableNameResolver) {
        try {
            this.tableNameResolver = tableNameResolver.newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("Cannot create a new instance of table name resolver for type '"
                    + tableNameResolver
                    + "'. Is default constructor available?"
            );
        }
    }

    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    public void setSqlGenerator(SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }
}

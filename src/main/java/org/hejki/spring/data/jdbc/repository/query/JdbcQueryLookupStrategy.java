package org.hejki.spring.data.jdbc.repository.query;

import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public final class JdbcQueryLookupStrategy {
    private static class CreateIfNotFoundQueryLookupStrategy implements QueryLookupStrategy {

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, NamedQueries namedQueries) {
            return null;
        }
    }

    public static QueryLookupStrategy create(QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
        Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");

        switch (key != null ? key : QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND) {
        case CREATE:
            return null;
        case USE_DECLARED_QUERY:
            return null;
        case CREATE_IF_NOT_FOUND:
            return new CreateIfNotFoundQueryLookupStrategy();
        default:
            throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
        }
    }
}

package org.hejki.spring.data.jdbc.repository.support;

import org.hejki.spring.data.jdbc.BeanPropertyRowUnmapper;
import org.hejki.spring.data.jdbc.JdbcRepository;
import org.hejki.spring.data.jdbc.RowUnmapper;
import org.hejki.spring.data.jdbc.repository.sql.SqlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public class SimpleJdbcRepository<T, ID extends Serializable> implements JdbcRepository<T, ID> {
    private static final Logger log = LoggerFactory.getLogger(SimpleJdbcRepository.class);

    private JdbcEntityInformation<T, ID> entityInfo;
    private JdbcOperations jdbcOperations;
    private SqlGenerator sqlGenerator;
    private RowMapper<T> rowMapper;
    private RowUnmapper<T> rowUnmapper;

    public SimpleJdbcRepository(JdbcEntityInformation<T, ID> entityInfo, JdbcOperations jdbcOperations) {
        this(entityInfo, jdbcOperations, null, null);
    }

    public SimpleJdbcRepository(JdbcEntityInformation<T, ID> entityInfo, JdbcOperations jdbcOperations,
                                Class<? extends RowMapper<T>> rowMapperClass) {
        this(entityInfo, jdbcOperations, rowMapperClass, null);
    }

    public SimpleJdbcRepository(JdbcEntityInformation<T, ID> entityInfo, JdbcOperations jdbcOperations,
                                Class<? extends RowMapper<T>> rowMapperClass,
                                Class<? extends RowUnmapper<T>> rowUnmapperClass) {
        this.entityInfo = entityInfo;
        this.jdbcOperations = jdbcOperations;
        setRowMapper(rowMapperClass);
        setRowUnmapper(rowUnmapperClass);
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return jdbcOperations.query(sqlGenerator.selectAll(entityInfo, sort), rowMapper);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        String query = sqlGenerator.selectAll(entityInfo, pageable);
        return new PageImpl<T>(jdbcOperations.query(query, rowMapper), pageable, count());
    }

    @Override
    public <S extends T> S save(S entity) {
        if (entityInfo.isNew(entity)) {
            return create(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        List<S> ret = new ArrayList<>();
        for (S s : entities) {
            ret.add(save(s));
        }
        return ret;
    }

    @Override
    public T findOne(ID id) {
        final List<T> entityOrEmpty = jdbcOperations.query(sqlGenerator.selectById(entityInfo), rowMapper, id);
        return entityOrEmpty.isEmpty() ? null : entityOrEmpty.get(0);
    }

    @Override
    public boolean exists(ID id) {
        return jdbcOperations.queryForObject(sqlGenerator.countById(entityInfo), Integer.class, id) > 0;
    }

    @Override
    public Iterable<T> findAll() {
        return jdbcOperations.query(sqlGenerator.selectAll(entityInfo), rowMapper);
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        final List<ID> idsList = toList(ids);

        if (idsList.isEmpty()) {
            return Collections.emptyList();
        }
        return jdbcOperations.query(sqlGenerator.selectByIds(entityInfo, idsList.size()), rowMapper, idsList.toArray());
    }

    @Override
    public long count() {
        return jdbcOperations.queryForObject(sqlGenerator.count(entityInfo), Long.class);
    }

    @Override
    public void delete(ID id) {
        jdbcOperations.update(sqlGenerator.deleteById(entityInfo), id);
    }

    @Override
    public void delete(T entity) {
        jdbcOperations.update(sqlGenerator.deleteById(entityInfo), entityInfo.getId(entity));
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        for (T t : entities) {
            delete(t);
        }
    }

    @Override
    public void deleteAll() {
        jdbcOperations.update(sqlGenerator.deleteAll(entityInfo));
    }

    // --- Hook methods ---

    protected Map<String,Object> preUpdate(T entity, Map<String, Object> columns) {
        return columns;
    }

    protected <S extends T> S postUpdate(S entity) {
        return entity;
    }

    protected Map<String, Object> preCreate(Map<String, Object> columns, T entity) {
        return columns;
    }

    protected <S extends T> S postCreate(S entity, Number generatedId) {
        return entity;
    }

    // --- Private methods ---

    private static <T> List<T> toList(Iterable<T> iterable) {
        final List<T> result = new ArrayList<>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }

    private <S extends T> S update(S entity) {
        final Map<String, Object> columns = preUpdate(entity, columns(entity));
        final Object idValue = removeIdColumns(columns);
        final String updateQuery = sqlGenerator.update(entityInfo, columns);

        columns.put(entityInfo.getIdColumn(), idValue);

        final Object[] queryParams = columns.values().toArray();
        jdbcOperations.update(updateQuery, queryParams);
        return postUpdate(entity);
    }

    private <S extends T> S create(S entity) {
        final Map<String, Object> columns = preCreate(columns(entity), entity);
        if (entityInfo.getId(entity) == null) {
            return createWithAutoGeneratedKey(entity, columns);
        } else {
            return createWithManuallyAssignedKey(entity, columns);
        }
    }

    private <S extends T> S createWithManuallyAssignedKey(S entity, Map<String, Object> columns) {
        final String createQuery = sqlGenerator.create(entityInfo, columns);
        final Object[] queryParams = columns.values().toArray();

        jdbcOperations.update(createQuery, queryParams);
        return postCreate(entity, null);
    }

    private <S extends T> S createWithAutoGeneratedKey(S entity, Map<String, Object> columns) {
        removeIdColumns(columns);
        final String createQuery = sqlGenerator.create(entityInfo, columns);
        final Object[] queryParams = columns.values().toArray();
        final GeneratedKeyHolder key = new GeneratedKeyHolder();

        jdbcOperations.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                final String idColumnName = entityInfo.getIdColumn();
                final PreparedStatement ps = con.prepareStatement(createQuery, new String[] { idColumnName });

                for (int i = 0; i < queryParams.length; ++i) {
                    ps.setObject(i + 1, queryParams[i]);
                }
                return ps;
            }
        }, key);
        return postCreate(entity, key.getKey());
    }

    private Object removeIdColumns(Map<String, Object> columns) {
        return columns.remove(entityInfo.getIdColumn());
    }

    private Map<String, Object> columns(T entity) {
        return new LinkedHashMap<>(rowUnmapper.mapColumns(entity));
    }

    private <T> T createInstance(Class<T> type) {
        try {
            Constructor<T> constructor;
            try {
                constructor = type.getConstructor(Class.class);
                return constructor.newInstance(entityInfo.getJavaType());
            } catch (NoSuchMethodException e) {
                constructor = type.getConstructor();
                return constructor.newInstance();
            }
        } catch (Exception e) {
            throw new BeanCreationException("Cannot create row mapper/unmapper for ", e); //TODO
        }
    }

    // --- Setters ---

    void setSqlGenerator(SqlGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }

    void setRowMapper(Class<? extends RowMapper> rowMapperClass) {
        if (null == rowMapperClass) {
            rowMapperClass = BeanPropertyRowMapper.class;
        }

        rowMapper = createInstance(rowMapperClass);
    }

    void setRowUnmapper(Class<? extends RowUnmapper> rowUnmapperClass) {
        if (null == rowUnmapperClass) {
            rowUnmapperClass = BeanPropertyRowUnmapper.class;
        }

        rowUnmapper = createInstance(rowUnmapperClass);
    }
}

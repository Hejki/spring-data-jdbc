package org.hejki.spring.data.jdbc.repository.support;

import org.hejki.spring.data.jdbc.BeanPropertyRowUnmapper;
import org.hejki.spring.data.jdbc.JdbcRepository;
import org.hejki.spring.data.jdbc.RowUnmapper;
import org.hejki.spring.data.jdbc.repository.config.JdbcRepositoryConfig;
import org.hejki.spring.data.jdbc.repository.config.JdbcRepositoryConfigUtil;
import org.hejki.spring.data.jdbc.repository.query.JdbcQueryLookupStrategy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.io.Serializable;

/**
 * JPA specific generic repository factory.
 * 
 * @author Hejki
 */
public class JdbcRepositoryFactory extends RepositoryFactorySupport {

	private final JdbcOperations jdbcOperations;
	private final JdbcRepositoryConfigUtil jdbcRepositoryConfigUtil;

	/**
	 * Creates a new {@link JdbcRepositoryFactory}.
	 *
	 * @param jdbcOperations must not be {@literal null}
	 * @param jdbcRepositoryConfigUtil
	 */
	public JdbcRepositoryFactory(JdbcOperations jdbcOperations, JdbcRepositoryConfigUtil jdbcRepositoryConfigUtil) {
		this.jdbcOperations = jdbcOperations;
		this.jdbcRepositoryConfigUtil = jdbcRepositoryConfigUtil;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
	 */
	@Override
	protected Object getTargetRepository(RepositoryMetadata metadata) {

		SimpleJdbcRepository<?, ?> repository = getTargetRepository(metadata, jdbcOperations);

		repository.setSqlGenerator(jdbcRepositoryConfigUtil.getSqlGenerator());
		return repository;
	}

	/**
	 * Callback to create a {@link JdbcRepository} instance with the given {@link JdbcOperations}
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param jdbcOperations
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T, ID extends Serializable> SimpleJdbcRepository<?, ?> getTargetRepository(RepositoryMetadata metadata,
			JdbcOperations jdbcOperations) {

		JdbcEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
		Class<? extends RowMapper> rowMapperClass = getRepositoryRowMapper(metadata);
		Class<? extends RowUnmapper> rowUnmapperClass = getRepositoryRowUnmapper(metadata);

		return new SimpleJdbcRepository(entityInformation, jdbcOperations, rowMapperClass, rowUnmapperClass);
	}

	protected Class<? extends RowMapper> getRepositoryRowMapper(RepositoryMetadata metadata) {
		JdbcRepositoryConfig annotation = AnnotationUtils.findAnnotation(metadata.getRepositoryInterface(), JdbcRepositoryConfig.class);

		if (null != annotation) {
			return annotation.rowMapper();
		}
		return BeanPropertyRowMapper.class;
	}

	protected Class<? extends RowUnmapper> getRepositoryRowUnmapper(RepositoryMetadata metadata) {
		JdbcRepositoryConfig annotation = AnnotationUtils.findAnnotation(metadata.getRepositoryInterface(), JdbcRepositoryConfig.class);

		if (null != annotation) {
			return annotation.rowUnmapper();
		}
		return BeanPropertyRowUnmapper.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.support.RepositoryFactorySupport#
	 * getRepositoryBaseClass()
	 */
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleJdbcRepository.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getQueryLookupStrategy(org.springframework.data.repository.query.QueryLookupStrategy.Key, org.springframework.data.repository.query.EvaluationContextProvider)
	 */
	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key, EvaluationContextProvider evaluationContextProvider) {
		return JdbcQueryLookupStrategy.create(key, evaluationContextProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.support.RepositoryFactorySupport#
	 * getEntityInformation(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> JdbcEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return new JdbcEntityInformation<>(domainClass, jdbcRepositoryConfigUtil);
	}
}

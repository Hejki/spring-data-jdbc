package org.hejki.spring.data.jdbc.repository.support;

import org.hejki.spring.data.jdbc.repository.config.JdbcRepositoryConfigUtil;
import org.hejki.spring.data.jdbc.repository.sql.SqlGenerator;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 * 
 * @author Hejki
 * @param <T> the type of the repository
 */
public class JdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends
		TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

	private JdbcOperations jdbcOperations;
	private JdbcRepositoryConfigUtil jdbcRepositoryConfigUtil;
	private SqlGenerator sqlGenerator;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#setMappingContext(org.springframework.data.mapping.context.MappingContext)
	 */
	@Override
	public void setMappingContext(MappingContext<?, ?> mappingContext) {
		super.setMappingContext(mappingContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.support.
	 * TransactionalRepositoryFactoryBeanSupport#doCreateRepositoryFactory()
	 */
	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		jdbcRepositoryConfigUtil.setSqlGenerator(sqlGenerator);
		return new JdbcRepositoryFactory(jdbcOperations, jdbcRepositoryConfigUtil);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(jdbcOperations, "JdbcOperations must not be null!");
		super.afterPropertiesSet();
	}

	public void setJdbcOperations(JdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}

	public void setJdbcRepositoryConfigUtil(JdbcRepositoryConfigUtil jdbcRepositoryConfigUtil) {
		this.jdbcRepositoryConfigUtil = jdbcRepositoryConfigUtil;
	}

	public void setSqlGenerator(SqlGenerator sqlGenerator) {
		this.sqlGenerator = sqlGenerator;
	}
}

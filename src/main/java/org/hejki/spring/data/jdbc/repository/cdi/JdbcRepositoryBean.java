//package org.hejki.spring.data.jdbc.repository.cdi;
//
//import org.hejki.spring.data.jdbc.repository.support.JdbcRepositoryFactory;
//import org.springframework.data.repository.cdi.CdiRepositoryBean;
//import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
//import org.springframework.jdbc.core.JdbcOperations;
//import org.springframework.util.Assert;
//
//import javax.enterprise.context.spi.CreationalContext;
//import javax.enterprise.inject.spi.Bean;
//import javax.enterprise.inject.spi.BeanManager;
//import java.lang.annotation.Annotation;
//import java.util.Set;
//
///**
// * A bean which represents a JPA repository.
// *
// * @author Dirk Mahler
// * @author Oliver Gierke
// * @author Mark Paluch
// * @param <T> The type of the repository.
// */
//class JdbcRepositoryBean<T> extends CdiRepositoryBean<T> {
//
//	private final Bean<JdbcOperations> jdbcOperationsBean;
//
//	/**
//	 * Constructs a {@link JpaRepositoryBean}.
//	 *
//	 * @param beanManager must not be {@literal null}.
//	 * @param entityManagerBean must not be {@literal null}.
//	 * @param qualifiers must not be {@literal null}.
//	 * @param repositoryType must not be {@literal null}.
//	 * @param detector can be {@literal null}.
//	 */
//	JdbcRepositoryBean(BeanManager beanManager, Bean<JdbcOperations> jdbcOperationsBean, Set<Annotation> qualifiers,
//			Class<T> repositoryType, CustomRepositoryImplementationDetector detector) {
//
//		super(qualifiers, repositoryType, beanManager, detector);
//
//		Assert.notNull(jdbcOperationsBean);
//		this.jdbcOperationsBean = jdbcOperationsBean;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.repository.cdi.CdiRepositoryBean#create(javax.enterprise.context.spi.CreationalContext, java.lang.Class, java.lang.Object)
//	 */
//	@Override
//	public T create(CreationalContext<T> creationalContext, Class<T> repositoryType, Object customImplementation) {
//
//		// Get an instance from the associated entity manager bean.
//		JdbcOperations jdbcOperations = getDependencyInstance(jdbcOperationsBean, JdbcOperations.class);
//
//		// Create the JPA repository instance and return it.
//		JdbcRepositoryFactory factory = new JdbcRepositoryFactory(jdbcOperations, jdbcRepositoryConfigUtil);
//		return factory.getRepository(repositoryType, customImplementation);
//	}
//}

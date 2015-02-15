package org.hejki.spring.data.jdbc.repository.config;

import org.hejki.spring.data.jdbc.JdbcRepository;
import org.hejki.spring.data.jdbc.mapping.NameResolver;
import org.hejki.spring.data.jdbc.repository.sql.SqlGenerator;
import org.hejki.spring.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

import java.lang.annotation.Annotation;
import java.util.*;

import static org.hejki.spring.data.jdbc.repository.config.BeanDefinitionNames.JDBC_OPERATIONS_BEAN_NAME;
import static org.hejki.spring.data.jdbc.repository.config.BeanDefinitionNames.JDBC_SQL_GENERATOR;

/**
 * JPA specific configuration extension parsing custom attributes from the XML namespace and
 * {@link EnableJdbcRepositories} annotation.
 * 
 * @author Hejki
 */
public class JdbcRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

//	private static final Class<?> PAB_POST_PROCESSOR = PersistenceAnnotationBeanPostProcessor.class;
	private static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "JDBC";
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtension#getRepositoryInterface()
	 */
	public String getRepositoryFactoryClassName() {
		return JdbcRepositoryFactoryBean.class.getName();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#getModulePrefix()
	 */
	@Override
	protected String getModulePrefix() {
		return getModuleName().toLowerCase(Locale.US);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getIdentifyingAnnotations()
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
		return (List) Arrays.asList(Persistent.class);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getIdentifyingTypes()
	 */
	@Override
	protected Collection<Class<?>> getIdentifyingTypes() {
		return Collections.<Class<?>> singleton(JdbcRepository.class);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.RepositoryConfigurationSource)
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {
//		String transactionManagerRef = source.getAttribute("transactionManagerRef");
//		builder.addPropertyValue("transactionManager",
//				transactionManagerRef == null ? DEFAULT_TRANSACTION_MANAGER_BEAN_NAME : transactionManagerRef);
		builder.addPropertyReference("jdbcOperations", JDBC_OPERATIONS_BEAN_NAME);
		builder.addPropertyValue("jdbcRepositoryConfigUtil", createConfigUtil(source));
		builder.addPropertyReference("sqlGenerator", JDBC_SQL_GENERATOR);
	}

	protected JdbcRepositoryConfigUtil createConfigUtil(RepositoryConfigurationSource source) {
		JdbcRepositoryConfigUtil configUtil = new JdbcRepositoryConfigUtil();

		if (source instanceof AnnotationRepositoryConfigurationSource) {
			AnnotationRepositoryConfigurationSource annotationSource = (AnnotationRepositoryConfigurationSource) source;

			configUtil.setTableNameResolverClass(annotationSource.getAttributes().<NameResolver>getClass("tableNameResolver"));
		}

		//TODO xml repo config
		return configUtil;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#registerBeansForRoot(org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.data.repository.config.RepositoryConfigurationSource)
	 */
	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource config) {
		super.registerBeansForRoot(registry, config);

//		Object source = config.getSource();
//		registerWithSourceAndGeneratedBeanName(registry, new RootBeanDefinition(
//				EntityManagerBeanDefinitionRegistrarPostProcessor.class), source);

		if (!registry.containsBeanDefinition(JDBC_OPERATIONS_BEAN_NAME)) {
			registry.registerBeanDefinition(JDBC_OPERATIONS_BEAN_NAME, //
					new RootBeanDefinition(JdbcTemplateFactoryBean.class));
		}

		if (!registry.containsBeanDefinition(JDBC_SQL_GENERATOR)) {
			registry.registerBeanDefinition(JDBC_SQL_GENERATOR,
					new RootBeanDefinition(SqlGenerator.class));
		}
	}
}

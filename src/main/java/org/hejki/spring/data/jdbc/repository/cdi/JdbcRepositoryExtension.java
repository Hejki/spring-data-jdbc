//package org.hejki.spring.data.jdbc.repository.cdi;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import javax.enterprise.event.Observes;
//import javax.enterprise.inject.UnsatisfiedResolutionException;
//import javax.enterprise.inject.spi.AfterBeanDiscovery;
//import javax.enterprise.inject.spi.Bean;
//import javax.enterprise.inject.spi.BeanManager;
//import javax.enterprise.inject.spi.ProcessBean;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.repository.cdi.CdiRepositoryBean;
//import org.springframework.data.repository.cdi.CdiRepositoryExtensionSupport;
//import org.springframework.jdbc.core.JdbcOperations;
//
///**
// * A portable CDI extension which registers beans for Spring Data JPA repositories.
// *
// * @author Dirk Mahler
// * @author Oliver Gierke
// * @author Mark Paluch
// */
//public class JdbcRepositoryExtension extends CdiRepositoryExtensionSupport {
//	private static final Logger log = LoggerFactory.getLogger(JdbcRepositoryExtension.class);
//
//	private final Map<Set<Annotation>, Bean<JdbcOperations>> jdbcOperations = new HashMap<>();
//
//	public JdbcRepositoryExtension() {
//		log.info("Activating CDI extension for Spring Data JDBC repositories.");
//	}
//
//	/**
//	 * Implementation of a an observer which checks for EntityManager beans and stores them in {@link #entityManagers} for
//	 * later association with corresponding repository beans.
//	 *
//	 * @param <X> The type.
//	 * @param processBean The annotated type as defined by CDI.
//	 */
//	@SuppressWarnings("unchecked")
//	<X> void processBean(@Observes ProcessBean<X> processBean) {
//		Bean<X> bean = processBean.getBean();
//		for (Type type : bean.getTypes()) {
//			// Check if the bean is an JdbcOperations.
//			if (type instanceof Class<?> && JdbcOperations.class.isAssignableFrom((Class<?>) type)) {
//				Set<Annotation> qualifiers = new HashSet<Annotation>(bean.getQualifiers());
//				if (bean.isAlternative() || !jdbcOperations.containsKey(qualifiers)) {
//					log.debug("Discovered '{}' with qualifiers {}.", JdbcOperations.class.getName(), qualifiers);
//					jdbcOperations.put(qualifiers, (Bean<JdbcOperations>) bean);
//				}
//			}
//		}
//	}
//
//	/**
//	 * Implementation of a an observer which registers beans to the CDI container for the detected Spring Data
//	 * repositories.
//	 * <p>
//	 * The repository beans are associated to the EntityManagers using their qualifiers.
//	 *
//	 * @param beanManager The BeanManager instance.
//	 */
//	void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
//
//		for (Entry<Class<?>, Set<Annotation>> entry : getRepositoryTypes()) {
//
//			Class<?> repositoryType = entry.getKey();
//			Set<Annotation> qualifiers = entry.getValue();
//
//			// Create the bean representing the repository.
//			CdiRepositoryBean<?> repositoryBean = createRepositoryBean(repositoryType, qualifiers, beanManager);
//			log.info("Registering bean for '{}' with qualifiers {}.", repositoryType.getName(), qualifiers);
//
//			// Register the bean to the extension and the container.
//			registerBean(repositoryBean);
//			afterBeanDiscovery.addBean(repositoryBean);
//		}
//	}
//
//	/**
//	 * Creates a {@link Bean}.
//	 *
//	 * @param <T> The type of the repository.
//	 * @param repositoryType The class representing the repository.
//	 * @param beanManager The BeanManager instance.
//	 * @return The bean.
//	 */
//	private <T> CdiRepositoryBean<T> createRepositoryBean(Class<T> repositoryType, Set<Annotation> qualifiers,
//			BeanManager beanManager) {
//		// Determine the entity manager bean which matches the qualifiers of the repository.
//		Bean<JdbcOperations> jdbcOperationsBean = jdbcOperations.get(qualifiers);
//
//		if (jdbcOperationsBean == null) {
//			throw new UnsatisfiedResolutionException(String.format("Unable to resolve a bean for '%s' with qualifiers %s.",
//					JdbcOperations.class.getName(), qualifiers));
//		}
//
//		// Construct and return the repository bean.
//		return new JdbcRepositoryBean<>(beanManager, jdbcOperationsBean, qualifiers, repositoryType,
//				getCustomImplementationDetector());
//	}
//}

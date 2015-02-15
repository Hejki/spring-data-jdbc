package org.hejki.spring.data.jdbc;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
@NoRepositoryBean
public interface JdbcRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
}

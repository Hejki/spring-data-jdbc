package org.hejki.spring.data.jdbc.repository;

import org.hejki.spring.data.jdbc.model.EntityModel;
import org.springframework.data.repository.CrudRepository;

/**
 * TODO Document me.
 *
 * @author Hejki
 */
public interface EntityRepository extends CrudRepository<EntityModel, Integer> {
}

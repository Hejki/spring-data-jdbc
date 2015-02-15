package org.hejki.spring.data.jdbc.repository.sql;

import org.springframework.data.domain.Pageable;

abstract class AbstractMssqlSqlGenerator extends SqlGenerator {
	public AbstractMssqlSqlGenerator() {
	}

	public AbstractMssqlSqlGenerator(String allColumnsClause) {
		super(allColumnsClause);
	}

	@Override
	protected String limitClause(Pageable page) {
		return "";
	}
}

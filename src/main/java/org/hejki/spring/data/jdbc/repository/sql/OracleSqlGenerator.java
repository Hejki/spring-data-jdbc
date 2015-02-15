package org.hejki.spring.data.jdbc.repository.sql;

import org.hejki.spring.data.jdbc.repository.support.JdbcEntityInformation;
import org.springframework.data.domain.Pageable;

public class OracleSqlGenerator extends SqlGenerator {
	public OracleSqlGenerator() {
	}

	public OracleSqlGenerator(String allColumnsClause) {
		super(allColumnsClause);
	}

	@Override
	protected String limitClause(Pageable page) {
		return "";
	}

	@Override
	public String selectAll(JdbcEntityInformation ei, Pageable page) {
		return SQL99Helper.generateSelectAllWithPagination(ei, page, this);
	}
}

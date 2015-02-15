package org.hejki.spring.data.jdbc.repository.sql;

import org.hejki.spring.data.jdbc.repository.support.JdbcEntityInformation;
import org.springframework.data.domain.Pageable;

public class MssqlSqlGenerator extends AbstractMssqlSqlGenerator {
	public MssqlSqlGenerator() {
	}

	public MssqlSqlGenerator(String allColumnsClause) {
		super(allColumnsClause);
	}

	@Override
	public String selectAll(JdbcEntityInformation ei, Pageable page) {
		return SQL99Helper.generateSelectAllWithPagination(ei, page, this);
	}
}

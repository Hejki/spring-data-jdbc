package org.hejki.spring.data.jdbc.repository.sql;

import org.hejki.spring.data.jdbc.repository.support.JdbcEntityInformation;
import org.springframework.data.domain.Pageable;

public class DerbySqlGenerator extends SqlGenerator {

	public static final String ROW_NUM_COLUMN = "ROW_NUM";
	public static final String ROW_NUM_COLUMN_CLAUSE = "SELECT * FROM (SELECT ROW_NUMBER() OVER () AS " + ROW_NUM_COLUMN + ", t.* FROM (";

	public DerbySqlGenerator(String allColumnsClause) {
		super(allColumnsClause);
	}

	public DerbySqlGenerator() {
	}

	@Override
	public String selectAll(JdbcEntityInformation ei, Pageable page) {
		final int offset = page.getPageNumber() * page.getPageSize() + 1;
		return ROW_NUM_COLUMN_CLAUSE + super.selectAll(ei, page) + ") AS t) AS a WHERE " + ROW_NUM_COLUMN + " BETWEEN " + offset + " AND " + (offset + page.getPageSize() - 1);
	}

	@Override
	protected String limitClause(Pageable page) {
		return "";
	}
}

package org.hejki.spring.data.jdbc.repository.sql;

import org.hejki.spring.data.jdbc.repository.support.JdbcEntityInformation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlGenerator {

	public static final String WHERE = " WHERE ";
	public static final String AND = " AND ";
	public static final String OR = " OR ";
	public static final String SELECT = "SELECT ";
	public static final String FROM = "FROM ";
	public static final String DELETE = "DELETE ";
	public static final String COMMA = ", ";
	public static final String PARAM = " = ?";
	private String allColumnsClause;

	public SqlGenerator(String allColumnsClause) {
		this.allColumnsClause = allColumnsClause;
	}

	public SqlGenerator() {
		this("*");
	}

	public String count(JdbcEntityInformation ei) {
		return SELECT + "COUNT(*) " + FROM + ei.getTableName();
	}

	public String deleteById(JdbcEntityInformation ei) {
		return DELETE + FROM + ei.getTableName() + whereByIdClause(ei);
	}

	private String whereByIdClause(JdbcEntityInformation ei) {
		final StringBuilder whereClause = new StringBuilder(WHERE);

		whereClause.append(ei.getIdColumn()).append(PARAM);
		return whereClause.toString();
	}

	private String whereByIdsClause(JdbcEntityInformation ei, int idsCount) {
		return whereByIdsWithSingleIdColumn(idsCount, ei.getIdColumn());
	}

	private String whereByIdsWithMultipleIdColumns(int idsCount, List<String> idColumnNames) {
		int idColumnsCount = idColumnNames.size();
		final StringBuilder whereClause = new StringBuilder(WHERE);
		final int totalParams = idsCount * idColumnsCount;
		for (int idColumnIdx = 0; idColumnIdx < totalParams; idColumnIdx += idColumnsCount) {
			if (idColumnIdx > 0) {
				whereClause.append(OR);
			}
			whereClause.append("(");
			for (int i = 0; i < idColumnsCount; ++i) {
				if (i > 0) {
					whereClause.append(AND);
				}
				whereClause.append(idColumnNames.get(i)).append(" = ?");
			}
			whereClause.append(")");
		}
		return whereClause.toString();
	}

	private String whereByIdsWithSingleIdColumn(int idsCount, String idColumn) {
		final StringBuilder whereClause = new StringBuilder(WHERE);
		return whereClause.
				append(idColumn).
				append(" IN (").
				append(repeat("?", COMMA, idsCount)).
				append(")").
				toString();
	}

	public String selectAll(JdbcEntityInformation ei) {
		return SELECT + allColumnsClause + " " + FROM + ei.getTableName();
	}

	public String selectAll(JdbcEntityInformation ei, Pageable page) {
		return selectAll(ei, page.getSort()) + limitClause(page);
	}

	public String selectAll(JdbcEntityInformation ei, Sort sort) {
		return selectAll(ei) + sortingClauseIfRequired(sort);
	}

	protected String limitClause(Pageable page) {
		final int offset = page.getPageNumber() * page.getPageSize();
		return " LIMIT " + offset + COMMA + page.getPageSize();
	}

	public String selectById(JdbcEntityInformation ei) {
		return selectAll(ei) + whereByIdClause(ei);
	}

	public String selectByIds(JdbcEntityInformation ei, int idsCount) {
		switch (idsCount) {
			case 0:
				return selectAll(ei);
			case 1:
				return selectById(ei);
			default:
				return selectAll(ei) + whereByIdsClause(ei, idsCount);
		}
	}

	protected String sortingClauseIfRequired(Sort sort) {
		if (sort == null) {
			return "";
		}
		StringBuilder orderByClause = new StringBuilder();
		orderByClause.append(" ORDER BY ");
		for(Iterator<Sort.Order> iterator = sort.iterator(); iterator.hasNext();) {
			final Sort.Order order = iterator.next();
			orderByClause.
					append(order.getProperty()).
					append(" ").
					append(order.getDirection().toString());
			if (iterator.hasNext()) {
				orderByClause.append(COMMA);
			}
		}
		return orderByClause.toString();
	}

	public String update(JdbcEntityInformation ei, Map<String, Object> columns) {
		final StringBuilder updateQuery = new StringBuilder("UPDATE " + ei.getTableName() + " SET ");
		for(Iterator<Map.Entry<String,Object>> iterator = columns.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, Object> column = iterator.next();
			updateQuery.append(column.getKey()).append(" = ?");
			if (iterator.hasNext()) {
				updateQuery.append(COMMA);
			}
		}
		updateQuery.append(whereByIdClause(ei));
		return updateQuery.toString();
	}

	public String create(JdbcEntityInformation ei, Map<String, Object> columns) {
		final StringBuilder createQuery = new StringBuilder("INSERT INTO " + ei.getTableName() + " (");
		appendColumnNames(createQuery, columns.keySet());
		createQuery.append(")").append(" VALUES (");
		createQuery.append(repeat("?", COMMA, columns.size()));
		return createQuery.append(")").toString();
	}

	private void appendColumnNames(StringBuilder createQuery, Set<String> columnNames) {
		for(Iterator<String> iterator = columnNames.iterator(); iterator.hasNext();) {
			final String column = iterator.next();
			createQuery.append(column);
			if (iterator.hasNext()) {
				createQuery.append(COMMA);
			}
		}
	}

	// Unfortunately {@link org.apache.commons.lang3.StringUtils} not available
	private static String repeat(String s, String separator, int count) {
		StringBuilder string = new StringBuilder((s.length() + separator.length()) * count);
		while (--count > 0) {
			string.append(s).append(separator);
		}
		return string.append(s).toString();
	}


	public String deleteAll(JdbcEntityInformation ei) {
		return DELETE + FROM + ei.getTableName();
	}

	public String countById(JdbcEntityInformation ei) {
		return count(ei) + whereByIdClause(ei);
	}

	public String getAllColumnsClause() {
		return allColumnsClause;
	}
}

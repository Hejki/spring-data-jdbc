package org.hejki.spring.data.jdbc.repository.sql;

import org.hejki.spring.data.jdbc.repository.support.JdbcEntityInformation;
import org.springframework.data.domain.Pageable;

public class SQL99Helper {
	public static String ROW_NUM_WRAPPER = "SELECT a__.* FROM (SELECT row_number() OVER (ORDER BY %s) AS ROW_NUM,  t__.*  FROM   (%s) t__) a__ WHERE  a__.row_num BETWEEN %s AND %s";

	public static String generateSelectAllWithPagination(JdbcEntityInformation ei, Pageable page, SqlGenerator sqlGenerator) {
		final int beginOffset = page.getPageNumber() * page.getPageSize() + 1;
		final int endOffset = beginOffset + page.getPageSize() - 1;
		String orderByPart = page.getSort() != null ? page.getSort().toString().replace(":", "") : ei.getIdColumn();
		String selectAllPart = sqlGenerator.selectAll(ei);

		return String.format(ROW_NUM_WRAPPER, orderByPart, selectAllPart, beginOffset, endOffset);
	}
}

package programmers.team6.domain.admin.utils;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryDslExecutor {

	public <T> List<T> fetch(JPAQuery<T> query) {
		return query.fetch();
	}

	public <T> Page<T> fetchPage(JPAQuery<T> query, SimpleExpression<?> baseTableId, Pageable pageable) {
		List<T> content = query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = query.clone()
			.select(baseTableId.count()).fetchOne();

		return PageableExecutionUtils.getPage(content, pageable,
			() -> total);
	}

}

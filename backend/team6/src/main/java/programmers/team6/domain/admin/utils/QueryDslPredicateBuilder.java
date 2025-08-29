package programmers.team6.domain.admin.utils;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;

public class QueryDslPredicateBuilder {
	private BooleanBuilder predicates;

	private QueryDslPredicateBuilder() {
		this.predicates = new BooleanBuilder();
	}

	public static QueryDslPredicateBuilder builder() {
		return new QueryDslPredicateBuilder();
	}

	public QueryDslPredicateBuilder andDateRange(DateTimePath<LocalDateTime> from, DateTimePath<LocalDateTime> to,
		LocalDateTime start, LocalDateTime end) {
		andGreaterThanOrEqualTo(from, start);
		andLessThanOrEqualTo(to, end);
		return this;
	}

	public QueryDslPredicateBuilder andContainsIgnoreCase(StringPath data, String filterData) {
		if (filterData != null && !filterData.isEmpty()) {
			predicates.and(data.containsIgnoreCase(filterData));
		}
		return this;
	}

	public <T> QueryDslPredicateBuilder andEqual(SimpleExpression<T> data, T reqData) {
		if (reqData != null) {
			predicates.and(data.eq(reqData));
		}
		return this;
	}

	public <T extends Comparable> QueryDslPredicateBuilder andGreaterThanOrEqualTo(ComparableExpression<T> data,
		T reqData) {
		if (reqData != null) {
			predicates.and(data.goe(reqData));
		}
		return this;
	}

	public <T extends Comparable> QueryDslPredicateBuilder andLessThan(ComparableExpression<T> data, T reqData) {
		if (reqData != null) {
			predicates.and(data.lt(reqData));
		}
		return this;
	}

	public <T extends Comparable> QueryDslPredicateBuilder andLessThanOrEqualTo(ComparableExpression<T> data,
		T reqData) {
		if (reqData != null) {
			predicates.and(data.loe(reqData));
		}
		return this;
	}

	public <T> QueryDslPredicateBuilder andIn(SimpleExpression<T> data, List<T> reqData) {
		if (reqData != null && !reqData.isEmpty()) {
			predicates.and(data.in(reqData));
		}
		return this;
	}

	public BooleanBuilder build() {
		return predicates;
	}

}

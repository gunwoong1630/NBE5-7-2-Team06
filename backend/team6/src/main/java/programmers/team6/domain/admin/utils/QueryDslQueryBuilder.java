package programmers.team6.domain.admin.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class QueryDslQueryBuilder<T> {
	private JPAQuery<T> query;

	private QueryDslQueryBuilder(JPAQueryFactory jpaQueryFactory, EntityPathBase<T> from) {
		this.query = jpaQueryFactory.selectFrom(from);
	}

	public static <T> QueryDslQueryBuilder builder(JPAQueryFactory jpaQueryFactory, EntityPathBase<T> from) {
		return new QueryDslQueryBuilder(jpaQueryFactory, from);
	}

	public <T> QueryDslQueryBuilder select(Class<T> projectionType, Expression<?>... expressions) {
		this.query.select(Projections.constructor(projectionType, expressions));
		return this;
	}

	public QueryDslQueryBuilder join(JoinPair<?>... joinPairs) {
		for (JoinPair joinPair : joinPairs) {
			this.query.join(joinPair.joinedTableInBaseTable(), joinPair.joinedTable());
		}
		return this;
	}

	public QueryDslQueryBuilder where(BooleanBuilder predicate) {
		this.query.where(predicate);
		return this;
	}

	public QueryDslQueryBuilder orderBy(OrderPair... orders) {
		for (OrderPair order : orders) {
			this.query.orderBy(order.toOrderSpecifier());
		}
		return this;
	}

	public JPAQuery<T> build() {
		return this.query;
	}
}

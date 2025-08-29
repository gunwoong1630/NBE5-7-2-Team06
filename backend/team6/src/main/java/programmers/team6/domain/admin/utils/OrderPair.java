package programmers.team6.domain.admin.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpression;

public record OrderPair<T extends Comparable>(ComparableExpression<T> expression, Order order) {
	public OrderSpecifier<T> toOrderSpecifier() {
		return new OrderSpecifier<>(order, expression);
	}
}

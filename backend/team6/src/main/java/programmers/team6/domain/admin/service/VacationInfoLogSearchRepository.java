package programmers.team6.domain.admin.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import programmers.team6.domain.admin.utils.QueryDslExecutor;
import programmers.team6.domain.admin.utils.QueryDslPredicateBuilder;
import programmers.team6.domain.admin.utils.QueryDslQueryBuilder;
import programmers.team6.domain.vacation.entity.QVacationInfoLog;

@Repository
@RequiredArgsConstructor
public class VacationInfoLogSearchRepository {

	private final JPAQueryFactory queryFactory;

	public List<Long> queryContainVacationInfoMemberIds(LocalDateTime localDateTime, String code) {
		QVacationInfoLog vacationInfoLog = QVacationInfoLog.vacationInfoLog;

		BooleanBuilder predicates = QueryDslPredicateBuilder.builder()
			.andEqual(vacationInfoLog.vacationType, code)
			.andLessThanOrEqualTo(vacationInfoLog.logDate, localDateTime).build();
		JPAQuery query = QueryDslQueryBuilder.builder(queryFactory, vacationInfoLog)
			.select(Long.class, vacationInfoLog.memberId)
			.where(predicates).build();
		return QueryDslExecutor.fetch(query);
	}
}

package programmers.team6.domain.vacation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import programmers.team6.domain.admin.utils.JoinPair;
import programmers.team6.domain.admin.utils.QueryDslExecutor;
import programmers.team6.domain.admin.utils.QueryDslPredicateBuilder;
import programmers.team6.domain.admin.utils.QueryDslQueryBuilder;
import programmers.team6.domain.member.entity.QCode;
import programmers.team6.domain.member.entity.QDept;
import programmers.team6.domain.member.entity.QMember;
import programmers.team6.domain.vacation.dto.VacationRequestCalendarResponse;
import programmers.team6.domain.vacation.entity.QVacationRequest;
import programmers.team6.domain.vacation.enums.VacationRequestStatus;

@Repository
@RequiredArgsConstructor
public class VacationRequestSearchRepository {

	private final JPAQueryFactory queryFactory;

	public List<VacationRequestCalendarResponse> findApprovedVacationsByMonth(
		VacationRequestStatus status, LocalDateTime start, LocalDateTime end, Long deptId
	) {
		QVacationRequest vacationRequest = QVacationRequest.vacationRequest;
		QMember member = QMember.member;
		QDept dept = QDept.dept;
		QCode position = QCode.code1;  // member.position
		QCode type = QCode.code1;      // vacationRequest.type

		BooleanBuilder predicates = QueryDslPredicateBuilder.builder()
			.andEqual(vacationRequest.status, status)
			.andGreaterThanOrEqualTo(vacationRequest.from, start)
			.andLessThan(vacationRequest.to, end)
			.andEqual(dept.id, deptId).build();

		JPAQuery query = QueryDslQueryBuilder.builder(queryFactory, vacationRequest)
			.select(VacationRequestCalendarResponse.class,
				member.name,
				dept.deptName,
				type.name,
				position.name,
				vacationRequest.from,
				vacationRequest.to)
			.join(
				new JoinPair<>(vacationRequest.member, member),
				new JoinPair<>(member.dept, dept),
				new JoinPair<>(vacationRequest.type, type)
			)
			.where(predicates).build();

		return QueryDslExecutor.fetch(query);
	}
}

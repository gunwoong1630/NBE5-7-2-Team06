package programmers.team6.domain.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import programmers.team6.domain.admin.dto.AdminVacationSearchCondition;
import programmers.team6.domain.admin.dto.VacationRequestSearchResponse;
import programmers.team6.domain.admin.utils.JoinPair;
import programmers.team6.domain.admin.utils.OrderPair;
import programmers.team6.domain.admin.utils.QueryDslExecutor;
import programmers.team6.domain.admin.utils.QueryDslPredicateBuilder;
import programmers.team6.domain.admin.utils.QueryDslQueryBuilder;
import programmers.team6.domain.member.entity.QCode;
import programmers.team6.domain.member.entity.QDept;
import programmers.team6.domain.member.entity.QMember;
import programmers.team6.domain.vacation.entity.QApprovalStep;
import programmers.team6.domain.vacation.entity.QVacationRequest;

@Repository
@RequiredArgsConstructor
public class AdminVacationRequestSearchCustom {
	private final JPAQueryFactory queryFactory;

	/**
	 * ApprovalStep와 VacationRequest를 join하고 AdminVacationSearchCondition의 변수들을 통해 다중 필터 구현
	 * From<A,B> = 'from 엔티티'에서 엔티티 부분 , A타입의 객체부터 B타입의 속성을 탐색 (그래서 Root<A,A>이고 Join<A,B>임, Root와 Join 둘다 From 상속)
	 * SingularAttribute<C,D> = 메타모델에서 사용하는 단일 속성 타입 정보 , C = 특정 엔티티, D = C 엔티티의 필드 타입
	 * Path<X> = 메타모델 경로 혹은 루트로부터 탐색된 속성(특정 속성의 경로) , X = 속성 타입, 해당 path가 가르키는 최종 필드 타입
	 * @param searchCondition 검색 필터
	 * @param pageable 페이징 정보
	 * @return 검색 결과 페이지
	 */
	public Page<VacationRequestSearchResponse> search(AdminVacationSearchCondition searchCondition, Pageable pageable) {

		QApprovalStep approvalStep = QApprovalStep.approvalStep;
		QVacationRequest vacationRequest = QVacationRequest.vacationRequest;
		QMember member = QMember.member;
		QCode code = QCode.code1;
		QDept dept = QDept.dept;

		/** 필터링
		 * 1. 휴가 신청 범위
		 * 2. 특정 년도 혹은 특정 분기 (1,2,3,4,상,하반기)
		 * 3. 휴가 신청자 이름
		 * 4. 부서 이름
		 * 5. 휴가 종류
		 * 6. 휴가 신청자 포지션
		 * 7. 휴가 신청 상태
		 */
		BooleanBuilder condition = QueryDslPredicateBuilder.builder()
			.andDateRange(vacationRequest.from, vacationRequest.to, searchCondition.dateRange().start(),
				searchCondition.dateRange().end())
			.andDateRange(vacationRequest.from, vacationRequest.to,
				searchCondition.dateRange().quarter().getStart(searchCondition.dateRange().year()),
				searchCondition.dateRange().quarter().getEnd(searchCondition.dateRange().year()))
			.andContainsIgnoreCase(member.name, searchCondition.applicant().name())
			.andContainsIgnoreCase(dept.deptName, searchCondition.applicant().deptName())
			.andEqual(code.id, searchCondition.applicant().vacationTypeCodeId())
			.andEqual(member.position.id, searchCondition.applicant().positionCodeId())
			.andEqual(vacationRequest.status, searchCondition.vacationRequestStatus())
			.build();

		JPQLQuery<String> subQuery = JPAExpressions.select(
				Expressions.stringTemplate("group_concat({0})", approvalStep.member.name))
			.from(approvalStep)
			.where(approvalStep.vacationRequest.eq(vacationRequest));

		JPAQuery query = QueryDslQueryBuilder.builder(queryFactory, vacationRequest)
			.select(VacationRequestSearchResponse.class,
				vacationRequest.id,
				code.name,
				vacationRequest.from,
				vacationRequest.to,
				member.name,
				subQuery,
				dept.deptName,
				vacationRequest.status)
			.join(
				new JoinPair(vacationRequest.member, member),
				new JoinPair(member.dept, dept),
				new JoinPair(vacationRequest.type, code))
			.where(condition)
			.orderBy(new OrderPair(vacationRequest.createdAt, Order.DESC))
			.build();

		return QueryDslExecutor.fetchPage(query, vacationRequest, pageable);
	}
}

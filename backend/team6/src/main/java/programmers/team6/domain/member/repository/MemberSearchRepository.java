package programmers.team6.domain.member.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import programmers.team6.domain.admin.utils.QueryDslExecutor;
import programmers.team6.domain.admin.utils.QueryDslPredicateBuilder;
import programmers.team6.domain.admin.utils.QueryDslQueryBuilder;
import programmers.team6.domain.member.entity.Member;
import programmers.team6.domain.member.entity.QMember;
import programmers.team6.domain.member.enums.Role;

@Repository
@RequiredArgsConstructor
public class MemberSearchRepository {

	private final JPAQueryFactory queryFactory;

	public Page<Member> searchFrom(String name, Long deptId, Pageable pageable) {
		QMember member = QMember.member;
		BooleanBuilder predicates = QueryDslPredicateBuilder.builder()
			.andContainsIgnoreCase(member.name, name)
			.andEqual(member.dept.id, deptId)
			.andEqual(member.role, Role.USER).build();

		JPAQuery query = QueryDslQueryBuilder.builder(queryFactory, member)
			.where(predicates).build();

		return QueryDslExecutor.fetchPage(query, member.id, pageable);
	}

	public Page<Member> searchFrom(Long deptId, String name, List<Long> ids, Pageable pageable) {
		QMember member = QMember.member;
		BooleanBuilder predicates = QueryDslPredicateBuilder.builder()
			.andContainsIgnoreCase(member.name, name)
			.andEqual(member.dept.id, deptId)
			.andIn(member.id, ids).build();

		JPAQuery query = QueryDslQueryBuilder.builder(queryFactory, member)
			.where(predicates).build();

		return QueryDslExecutor.fetchPage(query, member.id, pageable);
	}

}

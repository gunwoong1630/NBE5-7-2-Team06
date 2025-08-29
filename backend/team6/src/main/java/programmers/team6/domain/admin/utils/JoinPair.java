package programmers.team6.domain.admin.utils;

import com.querydsl.core.types.dsl.EntityPathBase;

import programmers.team6.global.exception.code.ConflictErrorCode;
import programmers.team6.global.exception.customException.ConflictException;

public record JoinPair<T>(EntityPathBase<T> joinedTableInBaseTable, EntityPathBase<T> joinedTable) {
	public JoinPair {
		if (joinedTableInBaseTable == null || joinedTable == null) {
			throw new ConflictException(ConflictErrorCode.CONFLICT_SQL);
		}
		
	}
}

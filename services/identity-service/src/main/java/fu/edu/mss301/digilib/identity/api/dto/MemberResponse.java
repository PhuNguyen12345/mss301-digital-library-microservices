package fu.edu.mss301.digilib.identity.api.dto;

import fu.edu.mss301.digilib.identity.domain.entity.Member;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MemberResponse(
		UUID memberId,
		String membershipType,
		String memberCode,
		Integer borrowingLimit,
		Integer loanPeriodDays,
		LocalDateTime createdAt,
		LocalDateTime updatedDate,
		BigDecimal outstandingBalance) {

	public static MemberResponse from(Member member) {
		if (member == null) {
			return null;
		}
		return new MemberResponse(member.getMemberId(), member.getMembershipType(), member.getMemberCode(),
				member.getBorrowingLimit(), member.getLoanPeriodDays(), member.getCreatedAt(), member.getUpdatedDate(),
				member.getOutstandingBalance());
	}
}

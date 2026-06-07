package fu.edu.mss301.digilib.identity.api.dto;

import fu.edu.mss301.digilib.identity.domain.entity.Member;
import java.math.BigDecimal;
import java.util.UUID;

public record MemberBorrowingEligibilityResponse(UUID userId, String memberCode, boolean eligible, int activeLoans,
		Integer borrowingLimit, int availableSlots, BigDecimal outstandingBalance) {

	public static MemberBorrowingEligibilityResponse from(UUID userId, Member member, boolean eligible,
			int activeLoans, int availableSlots) {
		return new MemberBorrowingEligibilityResponse(userId, member.getMemberCode(), eligible, activeLoans,
				member.getBorrowingLimit(), availableSlots, member.getOutstandingBalance());
	}
}

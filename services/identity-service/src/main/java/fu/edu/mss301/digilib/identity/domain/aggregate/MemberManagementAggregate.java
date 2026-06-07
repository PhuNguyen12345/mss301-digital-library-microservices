package fu.edu.mss301.digilib.identity.domain.aggregate;

import fu.edu.mss301.digilib.identity.domain.entity.Member;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.math.BigDecimal;

public final class MemberManagementAggregate {

	private final User user;
	private final Member member;

	private MemberManagementAggregate(User user) {
		this.user = user;
		this.member = user.getMember();
		if (member == null) {
			throw new IllegalStateException("User is not registered as a member");
		}
	}

	public static MemberManagementAggregate from(User user) {
		return new MemberManagementAggregate(user);
	}

	public void updateMembership(String membershipType, Integer borrowingLimit, Integer loanPeriodDays) {
		member.updatePolicy(membershipType, borrowingLimit, loanPeriodDays);
	}

	public void charge(BigDecimal amount) {
		member.increaseOutstandingBalance(amount);
	}

	public void receivePayment(BigDecimal amount) {
		member.decreaseOutstandingBalance(amount);
	}

	public boolean canBorrow(int activeLoans) {
		return user.hasStatus("ACTIVE") && member.hasBorrowingCapacity(activeLoans);
	}

	public int availableBorrowingSlots(int activeLoans) {
		return member.availableBorrowingSlots(activeLoans);
	}

	public User user() {
		return user;
	}

	public Member member() {
		return member;
	}
}

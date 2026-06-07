package fu.edu.mss301.digilib.identity.domain.service;

import fu.edu.mss301.digilib.identity.domain.aggregate.MemberManagementAggregate;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class MemberManagementDomainService {

	public MemberManagementAggregate updateMembership(User user, String membershipType, Integer borrowingLimit,
			Integer loanPeriodDays) {
		MemberManagementAggregate aggregate = MemberManagementAggregate.from(user);
		aggregate.updateMembership(membershipType, borrowingLimit, loanPeriodDays);
		return aggregate;
	}

	public MemberManagementAggregate charge(User user, BigDecimal amount) {
		validatePositiveAmount(amount);
		MemberManagementAggregate aggregate = MemberManagementAggregate.from(user);
		aggregate.charge(amount);
		return aggregate;
	}

	public MemberManagementAggregate receivePayment(User user, BigDecimal amount) {
		validatePositiveAmount(amount);
		MemberManagementAggregate aggregate = MemberManagementAggregate.from(user);
		aggregate.receivePayment(amount);
		return aggregate;
	}

	public boolean canBorrow(User user, int activeLoans) {
		if (activeLoans < 0) {
			throw new IllegalArgumentException("Active loans cannot be negative");
		}
		return MemberManagementAggregate.from(user).canBorrow(activeLoans);
	}

	private void validatePositiveAmount(BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
	}
}

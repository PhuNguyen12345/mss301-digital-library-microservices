package fu.edu.mss301.digilib.identity.domain.aggregate;

import fu.edu.mss301.digilib.identity.domain.entity.Member;
import fu.edu.mss301.digilib.identity.domain.entity.Role;
import fu.edu.mss301.digilib.identity.domain.entity.User;
import fu.edu.mss301.digilib.identity.domain.entity.UserRole;
import fu.edu.mss301.digilib.identity.domain.vo.EmailAddress;
import fu.edu.mss301.digilib.identity.domain.vo.MemberCode;
import fu.edu.mss301.digilib.identity.domain.vo.PhoneNumber;
import fu.edu.mss301.digilib.identity.domain.vo.Username;
import java.math.BigDecimal;

public final class UserAccountAggregate {

	private final User user;

	private UserAccountAggregate(User user) {
		this.user = user;
	}

	public static UserAccountAggregate from(User user) {
		return new UserAccountAggregate(user);
	}

	public static UserAccountAggregate create(EmailAddress email, String firstName, String lastName, PhoneNumber phone,
			String avatarUrl, Username username, String userStatus, String authType, String membershipType,
			MemberCode memberCode, Integer borrowingLimit, Integer loanPeriodDays, BigDecimal outstandingBalance) {
		User user = new User(email.value(), firstName, lastName, phone.value(), avatarUrl, username.value(), userStatus,
				authType);
		user.setMember(new Member(user, membershipType, memberCode.value(), borrowingLimit, loanPeriodDays,
				outstandingBalance == null ? BigDecimal.ZERO : outstandingBalance));
		return new UserAccountAggregate(user);
	}

	public UserRole assignRole(Role role) {
		return new UserRole(user, role);
	}

	public User user() {
		return user;
	}
}

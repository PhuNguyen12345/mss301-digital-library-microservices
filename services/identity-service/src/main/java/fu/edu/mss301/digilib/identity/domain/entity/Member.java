package fu.edu.mss301.digilib.identity.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member {

	@Id
	@Column(name = "member_id", nullable = false, updatable = false)
	private UUID memberId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "membership_type", nullable = false)
	private String membershipType;

	@Column(name = "member_code", nullable = false)
	private String memberCode;

	@Column(name = "borrowing_limit")
	private Integer borrowingLimit;

	@Column(name = "loan_period_days")
	private Integer loanPeriodDays;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "outstanding_balance")
	private BigDecimal outstandingBalance;

	public Member(User user, String membershipType, String memberCode, Integer borrowingLimit,
			Integer loanPeriodDays, BigDecimal outstandingBalance) {
		this.user = user;
		this.membershipType = membershipType;
		this.memberCode = memberCode;
		this.borrowingLimit = borrowingLimit;
		this.loanPeriodDays = loanPeriodDays;
		this.outstandingBalance = outstandingBalance;
	}

	public void updatePolicy(String membershipType, Integer borrowingLimit, Integer loanPeriodDays) {
		this.membershipType = membershipType;
		this.borrowingLimit = borrowingLimit;
		this.loanPeriodDays = loanPeriodDays;
	}

	public void increaseOutstandingBalance(BigDecimal amount) {
		outstandingBalance = currentBalance().add(amount);
	}

	public void decreaseOutstandingBalance(BigDecimal amount) {
		outstandingBalance = currentBalance().subtract(amount);
	}

	public boolean hasBorrowingCapacity(int activeLoans) {
		return borrowingLimit == null || activeLoans < borrowingLimit;
	}

	public int availableBorrowingSlots(int activeLoans) {
		if (borrowingLimit == null) {
			return Integer.MAX_VALUE;
		}
		return Math.max(0, borrowingLimit - activeLoans);
	}

	private BigDecimal currentBalance() {
		return outstandingBalance == null ? BigDecimal.ZERO : outstandingBalance;
	}

	@PrePersist
	void prePersist() {
		if (memberId == null) {
			memberId = UUID.randomUUID();
		}
		createdAt = LocalDateTime.now();
		updatedDate = createdAt;
	}

	@PreUpdate
	void preUpdate() {
		updatedDate = LocalDateTime.now();
	}

}

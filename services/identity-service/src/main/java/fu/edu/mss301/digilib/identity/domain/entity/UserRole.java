package fu.edu.mss301.digilib.identity.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "role_id" }))
public class UserRole {

	@Id
	@Column(name = "user_role_id", nullable = false, updatable = false)
	private UUID userRoleId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(name = "assigned_at", nullable = false)
	private LocalDateTime assignedAt;

	protected UserRole() {
	}

	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}

	@PrePersist
	void prePersist() {
		if (userRoleId == null) {
			userRoleId = UUID.randomUUID();
		}
		assignedAt = LocalDateTime.now();
	}

	public UUID getUserRoleId() {
		return userRoleId;
	}

	public User getUser() {
		return user;
	}

	public Role getRole() {
		return role;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}
}

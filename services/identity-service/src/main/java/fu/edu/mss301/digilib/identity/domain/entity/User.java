package fu.edu.mss301.digilib.identity.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@Column(name = "user_id", nullable = false, updatable = false)
	private UUID userId;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "firstname", nullable = false)
	private String firstName;

	@Column(name = "lastname", nullable = false)
	private String lastName;

	@Column(name = "phone", unique = true)
	private String phone;

	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "user_status", nullable = false)
	private String userStatus;

	@Column(name = "auth_type", nullable = false)
	private String authType;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Member member;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserRole> userRoles = new LinkedHashSet<>();

	public User(String email, String firstName, String lastName, String phone, String avatarUrl,
			String username, String userStatus, String authType) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.username = username;
		this.userStatus = userStatus;
		this.authType = authType;
	}

	public void setMember(Member member) {
		this.member = member;
		if (member != null && member.getUser() != this) {
			member.setUser(this);
		}
	}

	public void addUserRole(UserRole userRole) {
		userRoles.add(userRole);
	}

	@PrePersist
	void prePersist() {
		if (userId == null) {
			userId = UUID.randomUUID();
		}
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}

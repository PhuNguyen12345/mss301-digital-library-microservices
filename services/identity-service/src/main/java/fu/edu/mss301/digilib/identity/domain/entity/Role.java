package fu.edu.mss301.digilib.identity.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

	@Id
	@Column(name = "role_id", nullable = false, updatable = false)
	private UUID roleId;

	@Column(name = "role_name", nullable = false, unique = true)
	private String roleName;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@OneToMany(mappedBy = "role")
	private Set<UserRole> userRoles = new LinkedHashSet<>();

	@OneToMany(mappedBy = "role")
	private Set<RolePermission> rolePermissions = new LinkedHashSet<>();

	public Role(String roleName, String description) {
		this.roleName = roleName;
		this.description = description;
	}

	public void addUserRole(UserRole userRole) {
		userRoles.add(userRole);
	}

	public void addRolePermission(RolePermission rolePermission) {
		rolePermissions.add(rolePermission);
	}

	@PrePersist
	void prePersist() {
		if (roleId == null) {
			roleId = UUID.randomUUID();
		}
	}
}

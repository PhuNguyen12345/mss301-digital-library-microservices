package fu.edu.mss301.digilib.identity.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
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

	protected Role() {
	}

	public Role(String roleName, String description) {
		this.roleName = roleName;
		this.description = description;
	}

	@PrePersist
	void prePersist() {
		if (roleId == null) {
			roleId = UUID.randomUUID();
		}
	}

	public UUID getRoleId() {
		return roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getDescription() {
		return description;
	}

	public Set<RolePermission> getRolePermissions() {
		return rolePermissions;
	}
}

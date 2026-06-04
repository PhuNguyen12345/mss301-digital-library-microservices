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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_permissions", uniqueConstraints = @UniqueConstraint(columnNames = { "role_id", "permission_id" }))
public class RolePermission {

	@Id
	@Column(name = "role_permission_id", nullable = false, updatable = false)
	private UUID rolePermissionId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "permission_id", nullable = false)
	private Permission permission;

	public RolePermission(Role role, Permission permission) {
		this.role = role;
		this.permission = permission;
		role.addRolePermission(this);
		permission.addRolePermission(this);
	}

	@PrePersist
	void prePersist() {
		if (rolePermissionId == null) {
			rolePermissionId = UUID.randomUUID();
		}
	}
}

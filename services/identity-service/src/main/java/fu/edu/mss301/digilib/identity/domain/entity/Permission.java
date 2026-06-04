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
@Table(name = "permissions")
public class Permission {

	@Id
	@Column(name = "permission_id", nullable = false, updatable = false)
	private UUID permissionId;

	@Column(name = "permission_name", nullable = false, unique = true)
	private String permissionName;

	@Column(name = "resource", nullable = false)
	private String resource;

	@Column(name = "action", nullable = false)
	private String action;

	@OneToMany(mappedBy = "permission")
	private Set<RolePermission> rolePermissions = new LinkedHashSet<>();

	public Permission(String permissionName, String resource, String action) {
		this.permissionName = permissionName;
		this.resource = resource;
		this.action = action;
	}

	public void addRolePermission(RolePermission rolePermission) {
		rolePermissions.add(rolePermission);
	}

	@PrePersist
	void prePersist() {
		if (permissionId == null) {
			permissionId = UUID.randomUUID();
		}
	}
}

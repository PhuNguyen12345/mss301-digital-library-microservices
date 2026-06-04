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

	protected Permission() {
	}

	public Permission(String permissionName, String resource, String action) {
		this.permissionName = permissionName;
		this.resource = resource;
		this.action = action;
	}

	@PrePersist
	void prePersist() {
		if (permissionId == null) {
			permissionId = UUID.randomUUID();
		}
	}

	public UUID getPermissionId() {
		return permissionId;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public String getResource() {
		return resource;
	}

	public String getAction() {
		return action;
	}
}

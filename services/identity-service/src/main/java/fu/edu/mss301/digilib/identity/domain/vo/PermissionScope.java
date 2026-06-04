package fu.edu.mss301.digilib.identity.domain.vo;

public record PermissionScope(String resource, String action) {

	public PermissionScope {
		resource = requireText(resource, "Permission resource is required");
		action = requireText(action, "Permission action is required");
	}

	private static String requireText(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}
}

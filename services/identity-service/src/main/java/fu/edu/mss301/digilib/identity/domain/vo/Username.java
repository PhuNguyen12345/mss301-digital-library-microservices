package fu.edu.mss301.digilib.identity.domain.vo;

public record Username(String value) {

	public Username {
		value = requireText(value, "Username is required");
	}

	private static String requireText(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}
}

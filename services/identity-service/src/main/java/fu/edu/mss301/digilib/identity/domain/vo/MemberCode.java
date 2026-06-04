package fu.edu.mss301.digilib.identity.domain.vo;

public record MemberCode(String value) {

	public MemberCode {
		value = requireText(value, "Member code is required");
	}

	private static String requireText(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}
}

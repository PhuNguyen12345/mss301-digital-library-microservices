package fu.edu.mss301.digilib.identity.domain.vo;

import java.util.regex.Pattern;

public record EmailAddress(String value) {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	public EmailAddress {
		value = requireText(value, "Email is required");
		if (!EMAIL_PATTERN.matcher(value).matches()) {
			throw new IllegalArgumentException("Email is invalid");
		}
	}

	private static String requireText(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}
}

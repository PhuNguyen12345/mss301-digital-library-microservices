package fu.edu.mss301.digilib.identity.domain.vo;

public record PhoneNumber(String value) {

	public PhoneNumber {
		if (value != null) {
			value = value.trim();
		}
	}

	public boolean isPresent() {
		return value != null && !value.isBlank();
	}
}

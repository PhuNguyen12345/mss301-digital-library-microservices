package fu.edu.mss301.digilib.catalog.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Isbn {

    @Column(name = "isbn", unique = true, length = 20)
    private String value;

    protected Isbn() {
    }

    public Isbn(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }

        if (value.length() > 20) {
            throw new IllegalArgumentException("ISBN cannot exceed 20 characters");
        }

        this.value = value;
    }
}
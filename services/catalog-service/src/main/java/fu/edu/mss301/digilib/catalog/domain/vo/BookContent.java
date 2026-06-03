package fu.edu.mss301.digilib.catalog.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class BookContent {

    @Column(length = 500, nullable = false)
    private String description;

    @Column(name = "cover_image_url", length = 255)
    private String coverImageUrl;

    protected BookContent() {

    }

    public BookContent(String description, String coverImageUrl) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        if (description.length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters");
        }

        this.description = description;
        this.coverImageUrl = coverImageUrl;
    }
}
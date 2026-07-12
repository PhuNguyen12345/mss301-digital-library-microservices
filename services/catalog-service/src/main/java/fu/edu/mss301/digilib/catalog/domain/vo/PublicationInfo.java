package fu.edu.mss301.digilib.catalog.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.Year;

@Getter
@Embeddable
public class PublicationInfo {

    @Column(length = 150, nullable = false)
    private String publisher;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(length = 50, nullable = false)
    private String edition;

    @Column(length = 50, nullable = false)
    private String language;

    protected PublicationInfo() {

    }

    public PublicationInfo(
            String publisher,
            Integer publicationYear,
            String edition,
            String language
    ) {
        if (publisher == null || publisher.trim().isEmpty()) {
            throw new IllegalArgumentException("Publisher cannot be empty");
        }

        if (publicationYear == null) {
            throw new IllegalArgumentException("Publication year cannot be null");
        }

        if (publicationYear > Year.now().getValue()) {
            throw new IllegalArgumentException("Publication year cannot be in the future");
        }

        if (edition == null || edition.trim().isEmpty()) {
            throw new IllegalArgumentException("Edition cannot be empty");
        }

        if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("Language cannot be empty");
        }

        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.edition = edition;
        this.language = language;
    }
}
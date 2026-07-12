package fu.edu.mss301.digilib.loan.infrastructure.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class BookCatalogClientAdapter {
    private final RestClient restClient;

    public BookCatalogClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${services.catalog.base-url}") String bookCatalogBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(bookCatalogBaseUrl)
                .build();
    }

    public Long reserveBook(Long bookId) {
        BookCopyPage response = restClient.get()
                .uri("/api/catalog/books/{bookId}/copies?size=100", bookId)
                .retrieve()
                .body(BookCopyPage.class);

        BookCopy availableCopy = response == null || response.content() == null
                ? null
                : response.content().stream()
                    .filter(copy -> "AVAILABLE".equalsIgnoreCase(copy.copyStatus()))
                    .findFirst()
                    .orElse(null);
        if (availableCopy == null) {
            throw new IllegalStateException("No available book copy");
        }

        updateStatus(availableCopy.copyId(), "BORROWED");
        return availableCopy.copyId();
    }

    public void releaseBook(Long copyId) {
        if (copyId == null) {
            return;
        }
        updateStatus(copyId, "AVAILABLE");
    }

    private void updateStatus(Long copyId, String status) {
        restClient.patch()
                .uri("/api/catalog/book-copies/{copyId}/status", copyId)
                .body(Map.of("copyStatus", status))
                .retrieve()
                .toBodilessEntity();
    }

    private record BookCopyPage(List<BookCopy> content) {}
    private record BookCopy(Long copyId, String copyStatus) {}
}

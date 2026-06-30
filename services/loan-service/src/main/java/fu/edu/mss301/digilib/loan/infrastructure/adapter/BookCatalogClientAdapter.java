package fu.edu.mss301.digilib.loan.infrastructure.adapter;

import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookCatalogClientAdapter {
    private final RestClient restClient;

    public BookCatalogClientAdapter(
            RestClient.Builder restClientBuilder,
//            @Value("${services.book-catalog.base-url}")
            String bookCatalogBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(bookCatalogBaseUrl)
                .build();
    }

    public boolean reserveBook(Long bookId) {
        BookReservationResponse response = restClient.post()
                .uri("/api/v1/books/{bookId}/reserve", bookId)
                .retrieve()
                .body(BookReservationResponse.class);

        return response != null && response.reserved();
    }

    public void releaseBook(Long bookId) {
        restClient.post()
                .uri("/api/v1/books/{bookId}/release", bookId)
                .retrieve()
                .toBodilessEntity();
    }

    private record BookReservationResponse(
            boolean reserved
    ) {
    }
}
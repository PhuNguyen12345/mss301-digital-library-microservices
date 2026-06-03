package fu.edu.mss301.digilib.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book_copies")
public class BookCopy {
    public enum CopyStatus {
        LOANED,
        AVAILABLE,
        OVERDUE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "copy_id")
    private Long copyId;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(name = "shelf_location", length = 50, nullable = false)
    private String shelfLocation;

    @Column(name = "acquisition_date", nullable = false)
    private LocalDate acquisitionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_status", length = 30, nullable = false)
    @Builder.Default
    private CopyStatus copyStatus = CopyStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}

package fu.edu.mss301.digilib.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "digital_resources")
public class DigitalResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "file_format", length = 20, nullable = false)
    private String fileFormat;

    @Column(name = "resource_url", length = 255, nullable = false)
    private String resourceUrl;

    @Column(name = "access_permission", length = 20, nullable = false)
    private String accessPermission;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}

package fu.edu.mss301.digilib.catalog.domain.aggregate;

import fu.edu.mss301.digilib.catalog.domain.entity.Book;
import fu.edu.mss301.digilib.catalog.domain.entity.BookAuditLog;
import fu.edu.mss301.digilib.catalog.domain.entity.BookCopy;
import fu.edu.mss301.digilib.catalog.domain.entity.Category;
import fu.edu.mss301.digilib.catalog.domain.entity.Classification;
import fu.edu.mss301.digilib.catalog.domain.entity.DigitalResource;
import fu.edu.mss301.digilib.catalog.domain.vo.BookContent;
import fu.edu.mss301.digilib.catalog.domain.vo.Isbn;
import fu.edu.mss301.digilib.catalog.domain.vo.PublicationInfo;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BookAggregate {

    private final Book book;

    private final Isbn isbn;

    private PublicationInfo publicationInfo;

    private BookContent bookContent;

    private final List<BookCopy> bookCopies = new ArrayList<>();

    private final List<DigitalResource> digitalResources = new ArrayList<>();

    private final List<BookAuditLog> auditLogs = new ArrayList<>();

    public BookAggregate(
            Isbn isbn,
            String title,
            String author,
            PublicationInfo publicationInfo,
            BookContent bookContent,
            Category category,
            Classification classification,
            Integer userId
    ) {
        validateTitle(title);
        validateAuthor(author);

        this.isbn = isbn;
        this.publicationInfo = publicationInfo;
        this.bookContent = bookContent;

        this.book = Book.builder()
                .isbn(isbn.getValue())
                .title(title)
                .author(author)
                .publisher(publicationInfo.getPublisher())
                .publicationYear(publicationInfo.getPublicationYear())
                .edition(publicationInfo.getEdition())
                .language(publicationInfo.getLanguage())
                .description(bookContent.getDescription())
                .coverImageUrl(bookContent.getCoverImageUrl())
                .availabilityStatus("AVAILABLE")
                .category(category)
                .classification(classification)
                .build();

        addAuditLog(BookAuditLog.AuditAction.CREATE, userId);
    }

    public void updateBookInformation(
            String title,
            String author,
            PublicationInfo publicationInfo,
            BookContent bookContent,
            Category category,
            Classification classification,
            Integer userId
    ) {
        validateTitle(title);
        validateAuthor(author);

        this.publicationInfo = publicationInfo;
        this.bookContent = bookContent;

        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publicationInfo.getPublisher());
        book.setPublicationYear(publicationInfo.getPublicationYear());
        book.setEdition(publicationInfo.getEdition());
        book.setLanguage(publicationInfo.getLanguage());
        book.setDescription(bookContent.getDescription());
        book.setCoverImageUrl(bookContent.getCoverImageUrl());
        book.setCategory(category);
        book.setClassification(classification);

        addAuditLog(BookAuditLog.AuditAction.UPDATE, userId);
    }

    public void addBookCopy(
            String barcode,
            String shelfLocation,
            LocalDate acquisitionDate
    ) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode cannot be empty");
        }

        if (shelfLocation == null || shelfLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Shelf location cannot be empty");
        }

        if (acquisitionDate == null) {
            throw new IllegalArgumentException("Acquisition date cannot be null");
        }

        BookCopy copy = BookCopy.builder()
                .barcode(barcode)
                .shelfLocation(shelfLocation)
                .acquisitionDate(acquisitionDate)
                .copyStatus(BookCopy.CopyStatus.AVAILABLE)
                .book(book)
                .build();

        bookCopies.add(copy);
        updateBookAvailabilityStatus();
    }

    public void addDigitalResource(
            String fileFormat,
            String resourceUrl,
            String accessPermission
    ) {
        if (fileFormat == null || fileFormat.trim().isEmpty()) {
            throw new IllegalArgumentException("File format cannot be empty");
        }

        if (resourceUrl == null || resourceUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource URL cannot be empty");
        }

        if (accessPermission == null || accessPermission.trim().isEmpty()) {
            throw new IllegalArgumentException("Access permission cannot be empty");
        }

        DigitalResource resource = DigitalResource.builder()
                .fileFormat(fileFormat)
                .resourceUrl(resourceUrl)
                .accessPermission(accessPermission)
                .book(book)
                .build();

        digitalResources.add(resource);
    }

    public void markCopyAsLoaned(Long copyId) {
        BookCopy copy = findCopyById(copyId);
        copy.setCopyStatus(BookCopy.CopyStatus.LOANED);
        updateBookAvailabilityStatus();
    }

    public void markCopyAsAvailable(Long copyId) {
        BookCopy copy = findCopyById(copyId);
        copy.setCopyStatus(BookCopy.CopyStatus.AVAILABLE);
        updateBookAvailabilityStatus();
    }

    public void markCopyAsOverdue(Long copyId) {
        BookCopy copy = findCopyById(copyId);
        copy.setCopyStatus(BookCopy.CopyStatus.OVERDUE);
        updateBookAvailabilityStatus();
    }

    public void addAuditLog(BookAuditLog.AuditAction action, Integer userId) {
        if (action == null) {
            throw new IllegalArgumentException("Audit action cannot be null");
        }

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        BookAuditLog log = BookAuditLog.builder()
                .action(action)
                .userId(userId)
                .changedAt(LocalDateTime.now())
                .book(book)
                .build();

        auditLogs.add(log);
    }

    public int getTotalCopies() {
        return bookCopies.size();
    }

    public int getAvailableCopies() {
        return (int) bookCopies.stream()
                .filter(copy -> copy.getCopyStatus() == BookCopy.CopyStatus.AVAILABLE)
                .count();
    }

    private void updateBookAvailabilityStatus() {
        boolean hasAvailableCopy = bookCopies.stream()
                .anyMatch(copy -> copy.getCopyStatus() == BookCopy.CopyStatus.AVAILABLE);

        book.setAvailabilityStatus(hasAvailableCopy ? "AVAILABLE" : "BORROWED");
    }

    private BookCopy findCopyById(Long copyId) {
        return bookCopies.stream()
                .filter(copy -> copy.getCopyId() != null && copy.getCopyId().equals(copyId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book copy not found"));
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
    }

    private void validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
    }
}
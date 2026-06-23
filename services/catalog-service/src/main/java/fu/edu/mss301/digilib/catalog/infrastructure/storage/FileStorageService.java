package fu.edu.mss301.digilib.catalog.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final Path uploadRoot;

    public FileStorageService(@Value("${catalog.upload-dir:uploads/catalog}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeBookCover(MultipartFile file, Long bookId) {
        String extension = getExtension(file);
        String contentType = getContentType(file);

        if (!IMAGE_EXTENSIONS.contains(extension) || !IMAGE_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPG, PNG, or WEBP images are allowed");
        }

        String filename = "book-" + bookId + "-cover-" + UUID.randomUUID() + "." + extension;
        store(file, uploadRoot.resolve("images"), filename);
        return "/files/images/" + filename;
    }

    public String storeDigitalResourcePdf(MultipartFile file, Long bookId) {
        String extension = getExtension(file);
        String contentType = getContentType(file);

        if (!"pdf".equals(extension) || !"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        String filename = "book-" + bookId + "-resource-" + UUID.randomUUID() + ".pdf";
        store(file, uploadRoot.resolve("resources"), filename);
        return "/files/resources/" + filename;
    }

    private void store(MultipartFile file, Path targetDirectory, String filename) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        try {
            Files.createDirectories(targetDirectory);
            Path targetFile = targetDirectory.resolve(filename).normalize();

            if (!targetFile.startsWith(targetDirectory)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not store file", exception);
        }
    }

    private String getExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("File extension is required");
        }

        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String getContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("File content type is required");
        }

        return contentType.toLowerCase(Locale.ROOT);
    }
}

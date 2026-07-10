package fu.edu.mss301.digilib.catalog.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final Path uploadRoot;

    public StaticResourceConfig(@Value("${catalog.upload-dir:uploads/catalog}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations(getResourceLocation());
    }

    private String getResourceLocation() {
        String location = uploadRoot.toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }
}

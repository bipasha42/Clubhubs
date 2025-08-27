package com.example.clubhub4.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

@Service
@Slf4j
public class ImageStorageService {

    @Value("${app.media.upload-dir:uploads}")
    private String uploadDirProp;

    private Path root;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/jpg"
    );

    @PostConstruct
    void init() {
        try {
            Path configured = Paths.get(uploadDirProp);
            if (configured.isAbsolute()) {
                root = configured.toAbsolutePath().normalize();
            } else {
                Path base = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                root = base.resolve(uploadDirProp).normalize();
            }
            Files.createDirectories(root);
            log.info("Image upload directory: {}", root);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize upload directory (app.media.upload-dir='" + uploadDirProp + "')",
                    e
            );
        }
    }

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!ALLOWED.contains(contentType)) {
            throw new IllegalArgumentException("Only JPG, PNG, WEBP, or GIF allowed");
        }

        String ext = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif"  -> ".gif";
            default -> "";
        };

        String filename = UUID.randomUUID() + ext;
        Path dest = root.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image", e);
        }

        // Public URL used by templates
        return "/uploads/" + filename;
    }
}
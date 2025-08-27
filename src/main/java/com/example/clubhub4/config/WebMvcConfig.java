package com.example.clubhub4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.media.upload-dir:uploads}")
    private String uploadDirProp;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path configured = Paths.get(uploadDirProp);
        Path root = configured.isAbsolute()
                ? configured.toAbsolutePath().normalize()
                : Paths.get(System.getProperty("user.dir")).toAbsolutePath().resolve(uploadDirProp).normalize();

        String location = "file:" + (root.toString().endsWith("/") ? root.toString() : root.toString() + "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
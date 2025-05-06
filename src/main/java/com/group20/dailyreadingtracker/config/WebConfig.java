package com.group20.dailyreadingtracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures web-specific settings and resource handling.
 * 
 * Primary Responsibilities:
 * - Serves uploaded files from external directory
 * - Maps "/uploads/**" to configured storage location
 * - Applies caching headers (1 hour)
 * 
 * Configuration:
 * - Uses "${app.uploads.dir}" to determine file storage location
 * - Automatically normalizes path endings
 * 
 * @author Sofiia Mamonova
 */

@Configuration
public class WebConfig implements WebMvcConfigurer{
    
    @Value("${app.uploads.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadLocation = "file:" + uploadDir + (uploadDir.endsWith("/") ? "" : "/");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600);
    }
}
package com.spring_commerce.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class AppUtils {
    private AppUtils() {
    }

    public static String uploadImage(String path, MultipartFile imageFile) throws IOException {

        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String randomId = UUID.randomUUID().toString();

        String newFileName = randomId + fileExtension;
        Path newFolder = Path.of(System.getProperty("user.dir"), path);

        if (!Files.exists(newFolder))
            Files.createDirectories(newFolder);

        Path newFile = newFolder.resolve(newFileName);

        Files.copy(imageFile.getInputStream(), newFile);

        return newFileName;

    }
}

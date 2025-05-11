package com.spring_commerce.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class AppUtils {
    private AppUtils() {
    }

    public static String uploadImage(String path, MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String dotExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String randomId = UUID.randomUUID().toString();
        String newFileName = randomId + dotExtension;

        Path folder = Paths.get(System.getProperty("user.dir"), path);
        Files.createDirectories(folder);

        Path destination = folder.resolve(newFileName);
        Files.copy(imageFile.getInputStream(), destination);

        return newFileName;
    }
}

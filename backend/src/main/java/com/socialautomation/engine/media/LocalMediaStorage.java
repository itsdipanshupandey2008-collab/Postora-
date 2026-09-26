package com.socialautomation.engine.media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocalMediaStorage implements MediaStorage {

    private final Path storageDirectory;

    public LocalMediaStorage(
            @Value("${media.storage.location:uploads}") String storageLocation) {

        this.storageDirectory = Path.of(storageLocation);

        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not initialize media storage",
                    exception
            );
        }
    }

    @Override
    public String store(
            InputStream inputStream,
            String contentType) throws IOException {

        String storageKey = UUID.randomUUID().toString();

        Path target = storageDirectory.resolve(storageKey);

        Files.copy(
                inputStream,
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        return storageKey;
    }

    @Override
    public void delete(String storageKey) throws IOException {

        Path target = storageDirectory.resolve(storageKey);

        Files.deleteIfExists(target);
    }
}
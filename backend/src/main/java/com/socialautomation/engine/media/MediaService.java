package com.socialautomation.engine.media;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.socialautomation.engine.user.User;
import com.socialautomation.engine.user.UserRepository;

@Service
public class MediaService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final MediaRepository mediaRepository;
    private final MediaStorage mediaStorage;
    private final UserRepository userRepository;
    private final Tika tika;

    public MediaService(
            MediaRepository mediaRepository,
            MediaStorage mediaStorage,
            UserRepository userRepository) {

        this.mediaRepository = mediaRepository;
        this.mediaStorage = mediaStorage;
        this.userRepository = userRepository;
        this.tika = new Tika();
    }

    public MediaAsset upload(MultipartFile file, String userEmail) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size cannot exceed 10 MB");
        }

        String detectedContentType;

        try (InputStream inputStream = file.getInputStream()) {
            detectedContentType = tika.detect(inputStream, file.getOriginalFilename());
        }

        if (!detectedContentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only valid image files are allowed");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String storageKey = mediaStorage.store(
                file.getInputStream(),
                detectedContentType
        );

        try {
            MediaAsset mediaAsset = new MediaAsset(
                    user,
                    file.getOriginalFilename(),
                    storageKey,
                    detectedContentType,
                    file.getSize()
            );

            return mediaRepository.save(mediaAsset);

        } catch (RuntimeException exception) {
            mediaStorage.delete(storageKey);
            throw exception;
        }
    }

    public List<MediaAsset> getAllForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mediaRepository.findAllByUserId(user.getId());
    }

    public MediaAsset getByIdForUser(Long mediaId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return mediaRepository.findByIdAndUserId(mediaId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Media not found"));
    }

    public void deleteForUser(Long mediaId, String userEmail) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MediaAsset mediaAsset = mediaRepository.findByIdAndUserId(
                mediaId,
                user.getId()
        ).orElseThrow(() -> new IllegalArgumentException("Media not found"));

        mediaStorage.delete(mediaAsset.getStorageKey());
        mediaRepository.delete(mediaAsset);
    }
}
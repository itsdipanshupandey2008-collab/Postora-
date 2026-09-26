package com.socialautomation.engine.media;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping
    public ResponseEntity<MediaResponse> upload(
            @RequestPart("file") MultipartFile file,
            Authentication authentication) throws IOException {

        MediaAsset mediaAsset = mediaService.upload(
                file,
                authentication.getName()
        );

        MediaResponse response = new MediaResponse(
                mediaAsset.getId(),
                mediaAsset.getOriginalFilename(),
                mediaAsset.getContentType(),
                mediaAsset.getFileSize()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MediaResponse>> getAll(
            Authentication authentication) {

        List<MediaResponse> response = mediaService
                .getAllForUser(authentication.getName())
                .stream()
                .map(mediaAsset -> new MediaResponse(
                        mediaAsset.getId(),
                        mediaAsset.getOriginalFilename(),
                        mediaAsset.getContentType(),
                        mediaAsset.getFileSize()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(
            @PathVariable Long id,
            Authentication authentication) {

        MediaAsset mediaAsset = mediaService.getByIdForUser(
                id,
                authentication.getName()
        );

        MediaResponse response = new MediaResponse(
                mediaAsset.getId(),
                mediaAsset.getOriginalFilename(),
                mediaAsset.getContentType(),
                mediaAsset.getFileSize()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        mediaService.deleteForUser(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
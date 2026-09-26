package com.socialautomation.engine.media;

public class MediaResponse {

    private final Long id;
    private final String originalFilename;
    private final String contentType;
    private final Long fileSize;

    public MediaResponse(
            Long id,
            String originalFilename,
            String contentType,
            Long fileSize) {

        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }
}

package com.socialautomation.engine.media;

import java.io.IOException;
import java.io.InputStream;

public interface MediaStorage {

    String store(InputStream inputStream, String contentType) throws IOException;

    void delete(String storageKey) throws IOException;
}
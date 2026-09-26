package com.socialautomation.engine.media;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<MediaAsset, Long> {

    List<MediaAsset> findAllByUserId(Long userId);

    Optional<MediaAsset> findByIdAndUserId(Long id, Long userId);
}
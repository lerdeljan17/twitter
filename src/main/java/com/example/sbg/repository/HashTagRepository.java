package com.example.sbg.repository;

import com.example.sbg.model.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    Optional<HashTag> findByHashTag(String hashTag);
}

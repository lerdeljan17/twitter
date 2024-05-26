package com.example.sbg.repository;

import com.example.sbg.model.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Page<Tweet> findByHashtags_HashTagInAndUsernameIn(List<String> hashtags, List<String> usernames, Pageable pageable);

    Page<Tweet> findByHashtags_HashTagIn(List<String> hashtags, Pageable pageable);

    Page<Tweet> findByUsernameIn(List<String> usernames, Pageable pageable);
}

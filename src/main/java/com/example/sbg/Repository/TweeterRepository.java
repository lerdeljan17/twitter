package com.example.sbg.Repository;

import com.example.sbg.Model.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweeterRepository extends JpaRepository<Tweet, Long> {
    Page<Tweet> findByUsername(String username, PageRequest pageRequest);

    Page<Tweet> findByHashtagsIn(List<String> hashtags, PageRequest pageRequest);

    Page<Tweet> findByUsernameIn(List<String> usernames, PageRequest pageRequest);

    Page<Tweet> findByHashtagsInAndUsernameIn(List<String> hashtags, List<String> usernames, PageRequest pageRequest);
}

package com.example.sbg.Services;

import com.example.sbg.Model.Tweet;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ITweeterService {

    Tweet createTweet(String username, String content, List<String> hashtags);

    void deleteTweet(Long id, String username);

    Page<Tweet> getTweetsByHashtagsAndUsernames(List<String> hashtags, List<String> usernames, int pageNumber, int pageSize);

    Page<Tweet> getTweetsByHashtags(List<String> hashtags, int pageNumber, int pageSize);

    Page<Tweet> getTweetsByUsernames(List<String> usernames, int pageNumber, int pageSize);

    Page<Tweet> getAllTweets(int pageNumber, int pageSize);
}

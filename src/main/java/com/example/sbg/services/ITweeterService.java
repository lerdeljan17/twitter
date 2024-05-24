package com.example.sbg.services;

import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.model.Tweet;

import java.util.List;

public interface ITweeterService {

    Tweet createTweet(String username, String content, List<String> hashtags);

    void deleteTweet(Long id, String username);

    TweetsPageResp getTweetsByHashtagsAndUsernames(List<String> hashtags, List<String> usernames, int pageNumber, int pageSize);

    TweetsPageResp getTweetsByHashtags(List<String> hashtags, int pageNumber, int pageSize);

    TweetsPageResp getTweetsByUsernames(List<String> usernames, int pageNumber, int pageSize);

    TweetsPageResp getAllTweets(int pageNumber, int pageSize);
}

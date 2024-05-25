package com.example.sbg.mappers;

import com.example.sbg.model.Tweet;
import com.example.sbg.api.models.PostTweetReq;
import com.example.sbg.api.models.TweetResp;

import java.time.LocalDateTime;
import java.util.List;

public class TweetMapper {

    public static TweetResp toTweetResp(Tweet tweet) {
        return new TweetResp(
                tweet.getId().toString(),
                tweet.getContent(),
                tweet.getHashtags(),
                tweet.getUsername(),
                tweet.getCreatedAt().toString()
        );
    }

    public static Tweet toTweetEntity(PostTweetReq postTweetReq, String username) {
        Tweet tweet = new Tweet();
        tweet.setContent(postTweetReq.getTweetBody());
        tweet.setHashtags(postTweetReq.getHashTags());
        tweet.setCreatedAt(LocalDateTime.now());
        tweet.setUsername(username);
        tweet.setUsername(username);
        return tweet;
    }

    public static List<TweetResp> toTweetRespList(List<Tweet> tweets) {
        return tweets.stream()
                .map(TweetMapper::toTweetResp)
                .toList();
    }
}


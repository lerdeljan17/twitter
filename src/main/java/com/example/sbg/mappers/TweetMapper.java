package com.example.sbg.mappers;

import com.example.sbg.api.models.PostTweetReq;
import com.example.sbg.api.models.TweetResp;
import com.example.sbg.model.HashTag;
import com.example.sbg.model.Tweet;
import com.example.sbg.repository.HashTagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TweetMapper {

    private static HashTagRepository hashTagRepository;

    public TweetMapper(HashTagRepository hashTagRepository) {
        TweetMapper.hashTagRepository = hashTagRepository;
    }

    public static TweetResp toTweetResp(Tweet tweet) {
        List<String> hashtags = tweet.getHashtags().stream()
                .map(HashTag::getHashTag)
                .collect(Collectors.toList());

        return new TweetResp(
                tweet.getId().toString(),
                tweet.getContent(),
                hashtags,
                tweet.getUsername(),
                tweet.getCreatedAt().toString()
        );
    }

    public static Tweet toTweetEntity(PostTweetReq postTweetReq, String username) {
        Tweet tweet = new Tweet();
        tweet.setContent(postTweetReq.getTweetBody());
        tweet.setHashtags(postTweetReq.getHashTags().stream()
                .map(tag -> hashTagRepository.findByHashTag(tag).orElseGet(() -> {
                    HashTag newTag = new HashTag();
                    newTag.setHashTag(tag);
                    return newTag;
                }))
                .collect(Collectors.toSet()));
        tweet.setCreatedAt(LocalDateTime.now());
        tweet.setUsername(username);
        return tweet;
    }

    public static List<TweetResp> toTweetRespList(List<Tweet> tweets) {
        return tweets.stream()
                .map(TweetMapper::toTweetResp)
                .collect(Collectors.toList());
    }
}

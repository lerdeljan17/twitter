package com.example.sbg.services.implementation;

import com.example.sbg.api.models.TweetResp;
import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.exceptions.BadRequestException;
import com.example.sbg.exceptions.ResourceNotFoundException;
import com.example.sbg.mappers.TweetMapper;
import com.example.sbg.model.HashTag;
import com.example.sbg.model.Tweet;
import com.example.sbg.repository.HashTagRepository;
import com.example.sbg.repository.TweetRepository;
import com.example.sbg.services.ITweeterService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TweetService implements ITweeterService {

    private final TweetRepository tweetRepository;
    private final HashTagRepository hashTagRepository;

    public TweetService(TweetRepository tweetRepository, HashTagRepository hashTagRepository) {
        this.tweetRepository = tweetRepository;
        this.hashTagRepository = hashTagRepository;
    }

    public Tweet createTweet(String username, String content, List<String> hashtags) {

        if (hashtags != null) {

            if (hashtags.size() > 5)
                throw new BadRequestException("Too many hash tags");

            for (String tag : hashtags) {
                if (!tag.matches("^#[a-zA-Z0-9_]*$")) {
                    throw new BadRequestException("Invalid hash tag: " + tag);
                }
            }
        }

        Set<HashTag> hashTagSet = new HashSet<>();
        if (hashtags != null) {
            for (String tag : hashtags) {
                HashTag hashTag = hashTagRepository.findByHashTag(tag).orElseGet(() -> {
                    HashTag newTag = new HashTag();
                    newTag.setHashTag(tag);
                    return newTag;
                });
                hashTagSet.add(hashTag);
            }
        }

        // Save hashtags to ensure they are persisted
        hashTagSet = hashTagSet.stream()
                .map(hashTagRepository::save)
                .collect(Collectors.toSet());

        Tweet tweet = new Tweet();
        tweet.setUsername(username);
        tweet.setContent(content);
        tweet.setHashtags(hashTagSet);
        tweet.setCreatedAt(LocalDateTime.now());

        return tweetRepository.save(tweet);
    }

    public void deleteTweet(Long id, String username) {
        Tweet tweet = tweetRepository.findById(id).orElseThrow(() -> new RuntimeException("Tweet not found"));
        if (!tweet.getUsername().equals(username)) {
            throw new ResourceNotFoundException("You are not authorized to delete this tweet");
        }
        tweetRepository.delete(tweet);
    }

    @Override
    public TweetsPageResp getTweetsByHashtagsAndUsernames(List<String> hashtags, List<String> usernames, int pageNumber, int pageSize) {
        var tweetPage = tweetRepository.findByHashtags_HashTagInAndUsernameIn(hashtags, usernames, PageRequest.of(pageNumber, pageSize));
        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, hashtags, usernames) : null);
    }

    @Override
    public TweetsPageResp getTweetsByHashtags(List<String> hashtags, int pageNumber, int pageSize) {
        var tweetPage = tweetRepository.findByHashtags_HashTagIn(hashtags, PageRequest.of(pageNumber, pageSize));
        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, hashtags, Collections.emptyList()) : null);
    }

    @Override
    public TweetsPageResp getTweetsByUsernames(List<String> usernames, int pageNumber, int pageSize) {
        var tweetPage = tweetRepository.findByUsernameIn(usernames, PageRequest.of(pageNumber, pageSize));
        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, Collections.emptyList(), usernames) : null);
    }

    @Override
    public TweetsPageResp getAllTweets(int pageNumber, int pageSize) {
        var tweetPage = tweetRepository.findAll(PageRequest.of(pageNumber, pageSize));
        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, Collections.emptyList(), Collections.emptyList()) : null);
    }
}


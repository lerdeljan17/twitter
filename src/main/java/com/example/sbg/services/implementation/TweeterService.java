package com.example.sbg.services.implementation;

import com.example.sbg.api.models.TweetResp;
import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.exceptions.BadRequestException;
import com.example.sbg.exceptions.ResourceNotFoundException;
import com.example.sbg.mappers.TweetMapper;
import com.example.sbg.model.Tweet;
import com.example.sbg.repository.TweeterRepository;
import com.example.sbg.services.ITweeterService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class TweeterService implements ITweeterService {

    private final TweeterRepository tweeterRepository;

    public TweeterService(TweeterRepository tweeterRepository) {
        this.tweeterRepository = tweeterRepository;
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

        Tweet tweet = new Tweet();
        tweet.setUsername(username);
        tweet.setContent(content);
        tweet.setHashtags(hashtags);
        tweet.setCreatedAt(LocalDateTime.now());

        return tweeterRepository.save(tweet);
    }

    public void deleteTweet(Long id, String username) {
        Tweet tweet = tweeterRepository.findById(id).orElseThrow(() -> new RuntimeException("Tweet not found"));
        if (!tweet.getUsername().equals(username)) {
            throw new ResourceNotFoundException("You are not authorized to delete this tweet");
        }
        tweeterRepository.delete(tweet);
    }

    @Override
    public TweetsPageResp getTweetsByHashtagsAndUsernames(List<String> hashtags, List<String> usernames, int pageNumber, int pageSize) {

        var tweetPage = tweeterRepository.findByHashtagsInAndUsernameIn(hashtags, usernames, PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, hashtags, usernames) : null);
    }

    @Override
    public TweetsPageResp getTweetsByHashtags(List<String> hashtags, int pageNumber, int pageSize) {
        var tweetPage = tweeterRepository.findByHashtagsIn(hashtags, PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, hashtags, Collections.emptyList()) : null);
    }

    @Override
    public TweetsPageResp getTweetsByUsernames(List<String> usernames, int pageNumber, int pageSize) {
        var tweetPage = tweeterRepository.findByUsernameIn(usernames, PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, Collections.emptyList(), usernames) : null);
    }

    @Override
    public TweetsPageResp getAllTweets(int pageNumber, int pageSize) {
        var tweetPage = tweeterRepository.findAll(PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        return new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize, Collections.emptyList(), Collections.emptyList()) : null);
    }
}


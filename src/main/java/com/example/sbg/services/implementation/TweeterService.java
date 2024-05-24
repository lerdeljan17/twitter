package com.example.sbg.services.implementation;

import com.example.sbg.api.models.TweetResp;
import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.mappers.TweetMapper;
import com.example.sbg.model.Tweet;
import com.example.sbg.repository.TweeterRepository;
import com.example.sbg.services.ITweeterService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TweeterService implements ITweeterService {

    private final TweeterRepository tweeterRepository;

    public TweeterService(TweeterRepository tweeterRepository) {
        this.tweeterRepository = tweeterRepository;
    }

    public Tweet createTweet(String username, String content, List<String> hashtags) {
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
            throw new RuntimeException("Unauthorized");
        }
        tweeterRepository.delete(tweet);
    }

    @Override
    public TweetsPageResp getTweetsByHashtagsAndUsernames(List<String> hashtags, List<String> usernames, int pageNumber, int pageSize) {

        var tweetPage = tweeterRepository.findByHashtagsInAndUsernameIn(hashtags, usernames, PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        TweetsPageResp tweetsPageResp = new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize) : null);

        return tweetsPageResp;
    }

    @Override
    public TweetsPageResp getTweetsByHashtags(List<String> hashtags, int pageNumber, int pageSize) {
        var tweetPage =  tweeterRepository.findByHashtagsIn(hashtags, PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        TweetsPageResp tweetsPageResp = new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize) : null);

        return tweetsPageResp;
    }

    @Override
    public TweetsPageResp getTweetsByUsernames(List<String> usernames, int pageNumber, int pageSize) {
        var tweetPage =  tweeterRepository.findByUsernameIn(usernames, PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        TweetsPageResp tweetsPageResp = new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize) : null);

        return tweetsPageResp;
    }

    @Override
    public TweetsPageResp getAllTweets(int pageNumber, int pageSize) {
        var tweetPage =  tweeterRepository.findAll(PageRequest.of(pageNumber, pageSize));

        List<TweetResp> tweetResponses = TweetMapper.toTweetRespList(tweetPage.getContent());

        TweetsPageResp tweetsPageResp = new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? TweetsPageResp.createNextPageUrl(pageNumber, pageSize) : null);

        return tweetsPageResp;
    }
}


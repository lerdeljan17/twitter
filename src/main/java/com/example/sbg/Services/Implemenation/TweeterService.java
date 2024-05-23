package com.example.sbg.Services.Implemenation;

import com.example.sbg.Model.Tweet;
import com.example.sbg.Repository.TweeterRepository;
import com.example.sbg.Services.ITweeterService;
import org.springframework.data.domain.Page;
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
    public Page<Tweet> getTweetsByHashtagsAndUsernames(List<String> hashtags, List<String> usernames, int pageNumber, int pageSize) {
        return tweeterRepository.findByHashtagsInAndUsernameIn(hashtags, usernames, PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Tweet> getTweetsByHashtags(List<String> hashtags, int pageNumber, int pageSize) {
        return tweeterRepository.findByHashtagsIn(hashtags, PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Tweet> getTweetsByUsernames(List<String> usernames, int pageNumber, int pageSize) {
        return tweeterRepository.findByUsernameIn(usernames, PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Tweet> getAllTweets(int pageNumber, int pageSize) {
        return tweeterRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }
}


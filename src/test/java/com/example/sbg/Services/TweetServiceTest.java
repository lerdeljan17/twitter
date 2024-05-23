package com.example.sbg.Services;

import com.example.sbg.Model.Tweet;
import com.example.sbg.Repository.TweeterRepository;
import com.example.sbg.Services.Implemenation.TweeterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class TweetServiceTest {

    @InjectMocks
    private TweeterService tweetService;

    @Mock
    private TweeterRepository tweetRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTweet() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Test tweet");
        tweet.setHashtags(Collections.singletonList("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        when(tweetRepository.save(any(Tweet.class))).thenReturn(tweet);

        Tweet createdTweet = tweetService.createTweet("user", "Test tweet", Collections.singletonList("#test"));

        assertNotNull(createdTweet);
        assertEquals("user", createdTweet.getUsername());
        assertEquals("Test tweet", createdTweet.getContent());
        assertEquals("#test", createdTweet.getHashtags().get(0));
    }

    @Test
    void testDeleteTweet() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");

        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet));

        tweetService.deleteTweet(1L, "user");

        verify(tweetRepository, times(1)).delete(tweet);
    }

    @Test
    void testGetTweetsWithHashtagsAndUsernames() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Test tweet");
        tweet.setHashtags(Collections.singletonList("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        when(tweetRepository.findByHashtagsInAndUsernameIn(anyList(), anyList(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(tweet)));

        Page<Tweet> tweets = tweetService.getTweetsByHashtagsAndUsernames(Collections.singletonList("#test"),
                Collections.singletonList("user"), 0, 10);

        assertNotNull(tweets);
        assertEquals(1, tweets.getSize());
        assertEquals("user", tweets.stream().findFirst().get().getUsername());
    }

    @Test
    void testGetTweetsWithHashtags() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Test tweet");
        tweet.setHashtags(Collections.singletonList("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        when(tweetRepository.findByHashtagsIn(anyList(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(tweet)));

        Page<Tweet> tweets = tweetService.getTweetsByHashtags(Collections.singletonList("#test"), 0, 10);

        assertNotNull(tweets);
        assertEquals(1, tweets.getSize());
        assertEquals("user", tweets.stream().findFirst().get().getUsername());
    }

    @Test
    void testGetTweetsWithUsernames() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Test tweet");
        tweet.setHashtags(Collections.singletonList("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        when(tweetRepository.findByUsernameIn(anyList(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(tweet)));

        Page<Tweet> tweets = tweetService.getTweetsByUsernames(Collections.singletonList("user"), 0, 10);

        assertNotNull(tweets);
        assertEquals(1, tweets.getSize());
        assertEquals("user", tweets.stream().findFirst().get().getUsername());
    }

    @Test
    void testGetTweetsWithoutHashtagsAndUsernames() {
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Test tweet");
        tweet.setHashtags(Collections.singletonList("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        when(tweetRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(tweet)));

        Page<Tweet> tweets = tweetService.getAllTweets(0, 10);

        assertNotNull(tweets);
        assertEquals(1, tweets.getSize());
        assertEquals("user", tweets.stream().findFirst().get().getUsername());
    }
}
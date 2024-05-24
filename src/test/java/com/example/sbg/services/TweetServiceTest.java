package com.example.sbg.services;

import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.mappers.TweetMapper;
import com.example.sbg.model.Tweet;
import com.example.sbg.repository.TweeterRepository;
import com.example.sbg.services.implementation.TweeterService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.sbg.api.models.TweetsPageResp.createNextPageUrl;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TweetServiceTest {


    // createTweet should save and return a Tweet with correct data
    @Test
    public void test_create_tweet_with_correct_data() {
        // Arrange
        TweeterRepository mockRepository = Mockito.mock(TweeterRepository.class);
        TweeterService tweeterService = new TweeterService(mockRepository);
        String username = "user1";
        String content = "Hello World!";
        List<String> hashtags = Arrays.asList("#hello", "#world");
        Tweet expectedTweet = new Tweet();
        expectedTweet.setUsername(username);
        expectedTweet.setContent(content);
        expectedTweet.setHashtags(hashtags);
        expectedTweet.setCreatedAt(LocalDateTime.now());
        Mockito.when(mockRepository.save(Mockito.any(Tweet.class))).thenReturn(expectedTweet);

        // Act
        Tweet result = tweeterService.createTweet(username, content, hashtags);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(content, result.getContent());
        assertTrue(result.getHashtags().containsAll(hashtags));
    }

    // deleteTweet should successfully remove a tweet if the username matches
    @Test
    public void test_delete_tweet_success() {
        // Arrange
        TweeterRepository mockRepository = Mockito.mock(TweeterRepository.class);
        TweeterService tweeterService = new TweeterService(mockRepository);
        Long tweetId = 1L;
        String username = "user1";
        Tweet tweet = new Tweet();
        tweet.setId(tweetId);
        tweet.setUsername(username);
        Mockito.when(mockRepository.findById(tweetId)).thenReturn(Optional.of(tweet));

        // Act
        tweeterService.deleteTweet(tweetId, username);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1)).delete(tweet);
    }

    // createTweet should handle null hashtags gracefully
    @Test
    public void test_create_tweet_with_null_hashtags() {
        // Arrange
        TweeterRepository mockRepository = Mockito.mock(TweeterRepository.class);
        TweeterService tweeterService = new TweeterService(mockRepository);
        String username = "user2";
        String content = "Test tweet";
        List<String> hashtags = null;
        Tweet expectedTweet = new Tweet();
        expectedTweet.setUsername(username);
        expectedTweet.setContent(content);
        expectedTweet.setHashtags(null);  // Expecting null to be handled gracefully
        expectedTweet.setCreatedAt(LocalDateTime.now());
        Mockito.when(mockRepository.save(Mockito.any(Tweet.class))).thenReturn(expectedTweet);

        // Act
        Tweet result = tweeterService.createTweet(username, content, hashtags);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(content, result.getContent());
        assertNull(result.getHashtags());
    }

    // getTweetsByHashtagsAndUsernames should return correct paginated results and next page URL
    @Test
    public void test_get_tweets_by_hashtags_and_usernames() {
        // Arrange

        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(List.of("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        TweeterRepository mockRepository = Mockito.mock(TweeterRepository.class);
        TweeterService tweeterService = new TweeterService(mockRepository);
        List<String> hashtags = Arrays.asList("#hello", "#world");
        List<String> usernames = Arrays.asList("user1", "user2");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Tweet> tweets = Arrays.asList(tweet, tweet);
        Page<Tweet> expectedPage = new PageImpl<>(tweets);
        Mockito.when(mockRepository.findByHashtagsInAndUsernameIn(hashtags, usernames, pageRequest)).thenReturn(expectedPage);

        var expectedResult = TweetMapper.toTweetRespList(expectedPage.getContent());

        TweetsPageResp tweetsPageRespExp = new TweetsPageResp(expectedResult,
                expectedPage.hasNext() ? TweetsPageResp.createNextPageUrl(0, 10) : null);

        // Act
        TweetsPageResp result = tweeterService.getTweetsByHashtagsAndUsernames(hashtags, usernames, 0, 10);

        // Assert
        assertEquals(tweetsPageRespExp, result);
    }

    // getTweetsByHashtags should return correct paginated results and next page URL
    @Test
    public void test_get_tweets_by_hashtags() {
        // Arrange
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(List.of("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        TweeterRepository mockRepository = Mockito.mock(TweeterRepository.class);
        TweeterService tweeterService = new TweeterService(mockRepository);
        List<String> hashtags = Arrays.asList("#hello", "#world");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Tweet> tweets = Arrays.asList(tweet, tweet);
        Page<Tweet> expectedPage = new PageImpl<>(tweets);
        Mockito.when(mockRepository.findByHashtagsIn(hashtags, pageRequest)).thenReturn(expectedPage);

        var expectedResult = TweetMapper.toTweetRespList(expectedPage.getContent());

        TweetsPageResp tweetsPageRespExp = new TweetsPageResp(expectedResult,
                expectedPage.hasNext() ? TweetsPageResp.createNextPageUrl(0, 10) : null);

        // Act
        TweetsPageResp result = tweeterService.getTweetsByHashtags(hashtags, 0, 10);

        // Assert
        assertEquals(tweetsPageRespExp, result);
    }

    // getTweetsByUsernames should return correct paginated results and next page URL
    @Test
    public void test_get_tweets_by_usernames() {
        // Arrange
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(List.of("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        TweeterRepository mockRepository = Mockito.mock(TweeterRepository.class);
        TweeterService tweeterService = new TweeterService(mockRepository);
        List<String> usernames = Arrays.asList("user1", "user2");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Tweet> tweets = Arrays.asList(tweet, tweet);
        Page<Tweet> expectedPage = new PageImpl<>(tweets);
        Mockito.when(mockRepository.findByUsernameIn(usernames, pageRequest)).thenReturn(expectedPage);

        var expectedResult = TweetMapper.toTweetRespList(expectedPage.getContent());

        TweetsPageResp tweetsPageRespExp = new TweetsPageResp(expectedResult,
                expectedPage.hasNext() ? TweetsPageResp.createNextPageUrl(0, 10) : null);
        // Act
        TweetsPageResp result = tweeterService.getTweetsByUsernames(usernames, 0, 10);

        // Assert
        assertEquals(tweetsPageRespExp, result);
    }

}
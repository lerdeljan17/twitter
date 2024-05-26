package com.example.sbg.services;

import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.mappers.TweetMapper;
import com.example.sbg.model.HashTag;
import com.example.sbg.model.Tweet;
import com.example.sbg.repository.HashTagRepository;
import com.example.sbg.repository.TweetRepository;
import com.example.sbg.services.implementation.TweetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TweetServiceTest {

    // createTweet should save and return a Tweet with correct data
    @Test
    public void test_create_tweet_with_correct_data() {
        // Arrange
        TweetRepository mockRepository = Mockito.mock(TweetRepository.class);
        HashTagRepository mockHashTagRepository = Mockito.mock(HashTagRepository.class);
        TweetService tweetService = new TweetService(mockRepository, mockHashTagRepository);
        String username = "user1";
        String content = "Hello World!";
        List<String> hashtags = Arrays.asList("#hello", "#world");

        Set<HashTag> hashTagSet = hashtags.stream().map(tag -> {
            HashTag hashTag = new HashTag();
            hashTag.setHashTag(tag);
            return hashTag;
        }).collect(Collectors.toSet());

        Tweet expectedTweet = new Tweet();
        expectedTweet.setUsername(username);
        expectedTweet.setContent(content);
        expectedTweet.setHashtags(hashTagSet);
        expectedTweet.setCreatedAt(LocalDateTime.now());

        Mockito.when(mockRepository.save(Mockito.any(Tweet.class))).thenReturn(expectedTweet);
        Mockito.when(mockHashTagRepository.findByHashTag(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(mockHashTagRepository.save(Mockito.any(HashTag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tweet result = tweetService.createTweet(username, content, hashtags);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(content, result.getContent());
        assertTrue(result.getHashtags().stream().map(HashTag::getHashTag).collect(Collectors.toSet()).containsAll(hashtags));
    }

    // deleteTweet should successfully remove a tweet if the username matches
    @Test
    public void test_delete_tweet_success() {
        // Arrange
        TweetRepository mockRepository = Mockito.mock(TweetRepository.class);
        HashTagRepository mockHashTagRepository = Mockito.mock(HashTagRepository.class);
        TweetService tweetService = new TweetService(mockRepository, mockHashTagRepository);
        Long tweetId = 1L;
        String username = "user1";
        Tweet tweet = new Tweet();
        tweet.setId(tweetId);
        tweet.setUsername(username);
        Mockito.when(mockRepository.findById(tweetId)).thenReturn(Optional.of(tweet));

        // Act
        tweetService.deleteTweet(tweetId, username);

        // Assert
        Mockito.verify(mockRepository, Mockito.times(1)).delete(tweet);
    }

    // createTweet should handle null hashtags gracefully
    @Test
    public void test_create_tweet_with_null_hashtags() {
        // Arrange
        TweetRepository mockRepository = Mockito.mock(TweetRepository.class);
        HashTagRepository mockHashTagRepository = Mockito.mock(HashTagRepository.class);
        TweetService tweetService = new TweetService(mockRepository, mockHashTagRepository);
        String username = "user2";
        String content = "Test tweet";
        List<String> hashtags = null;
        Tweet expectedTweet = new Tweet();
        expectedTweet.setUsername(username);
        expectedTweet.setContent(content);
        expectedTweet.setHashtags(new HashSet<>());  // Expecting an empty set for null hashtags
        expectedTweet.setCreatedAt(LocalDateTime.now());
        Mockito.when(mockRepository.save(Mockito.any(Tweet.class))).thenReturn(expectedTweet);

        // Act
        Tweet result = tweetService.createTweet(username, content, hashtags);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(content, result.getContent());
        assertTrue(result.getHashtags().isEmpty());
    }

    // getTweetsByHashtagsAndUsernames should return correct paginated results and next page URL
    @Test
    public void test_get_tweets_by_hashtags_and_usernames() {
        // Arrange
        HashTag hashTag = new HashTag();
        hashTag.setHashTag("#test");
        Set<HashTag> hashTagSet = new HashSet<>(Collections.singletonList(hashTag));

        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(hashTagSet);
        tweet.setCreatedAt(LocalDateTime.now());

        TweetRepository mockRepository = Mockito.mock(TweetRepository.class);
        HashTagRepository mockHashTagRepository = Mockito.mock(HashTagRepository.class);
        TweetService tweetService = new TweetService(mockRepository, mockHashTagRepository);
        List<String> hashtags = Arrays.asList("#hello", "#world");
        List<String> usernames = Arrays.asList("user1", "user2");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Tweet> tweets = Arrays.asList(tweet, tweet);
        Page<Tweet> expectedPage = new PageImpl<>(tweets);
        Mockito.when(mockRepository.findByHashtags_HashTagInAndUsernameIn(hashtags, usernames, pageRequest)).thenReturn(expectedPage);

        var expectedResult = TweetMapper.toTweetRespList(expectedPage.getContent());

        TweetsPageResp tweetsPageRespExp = new TweetsPageResp(expectedResult,
                expectedPage.hasNext() ? TweetsPageResp.createNextPageUrl(0, 10, hashtags, usernames) : null);

        // Act
        TweetsPageResp result = tweetService.getTweetsByHashtagsAndUsernames(hashtags, usernames, 0, 10);

        // Assert
        assertEquals(tweetsPageRespExp, result);
    }

    // getTweetsByHashtags should return correct paginated results and next page URL
    @Test
    public void test_get_tweets_by_hashtags() {
        // Arrange
        HashTag hashTag = new HashTag();
        hashTag.setHashTag("#test");
        Set<HashTag> hashTagSet = new HashSet<>(Collections.singletonList(hashTag));

        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(hashTagSet);
        tweet.setCreatedAt(LocalDateTime.now());

        TweetRepository mockRepository = Mockito.mock(TweetRepository.class);
        HashTagRepository mockHashTagRepository = Mockito.mock(HashTagRepository.class);
        TweetService tweetService = new TweetService(mockRepository, mockHashTagRepository);
        List<String> hashtags = Arrays.asList("#hello", "#world");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Tweet> tweets = Arrays.asList(tweet, tweet);
        Page<Tweet> expectedPage = new PageImpl<>(tweets);
        Mockito.when(mockRepository.findByHashtags_HashTagIn(hashtags, pageRequest)).thenReturn(expectedPage);

        var expectedResult = TweetMapper.toTweetRespList(expectedPage.getContent());

        TweetsPageResp tweetsPageRespExp = new TweetsPageResp(expectedResult,
                expectedPage.hasNext() ? TweetsPageResp.createNextPageUrl(0, 10, hashtags, Collections.emptyList()) : null);

        // Act
        TweetsPageResp result = tweetService.getTweetsByHashtags(hashtags, 0, 10);

        // Assert
        assertEquals(tweetsPageRespExp, result);
    }

    // getTweetsByUsernames should return correct paginated results and next page URL
    @Test
    public void test_get_tweets_by_usernames() {
        // Arrange
        HashTag hashTag = new HashTag();
        hashTag.setHashTag("#test");
        Set<HashTag> hashTagSet = new HashSet<>(Collections.singletonList(hashTag));

        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(hashTagSet);
        tweet.setCreatedAt(LocalDateTime.now());

        TweetRepository mockRepository = Mockito.mock(TweetRepository.class);
        HashTagRepository mockHashTagRepository = Mockito.mock(HashTagRepository.class);
        TweetService tweetService = new TweetService(mockRepository, mockHashTagRepository);
        List<String> usernames = Arrays.asList("user1", "user2");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Tweet> tweets = Arrays.asList(tweet, tweet);
        Page<Tweet> expectedPage = new PageImpl<>(tweets);
        Mockito.when(mockRepository.findByUsernameIn(usernames, pageRequest)).thenReturn(expectedPage);

        var expectedResult = TweetMapper.toTweetRespList(expectedPage.getContent());

        TweetsPageResp tweetsPageRespExp = new TweetsPageResp(expectedResult,
                expectedPage.hasNext() ? TweetsPageResp.createNextPageUrl(0, 10, Collections.emptyList(), usernames) : null);
        // Act
        TweetsPageResp result = tweetService.getTweetsByUsernames(usernames, 0, 10);

        // Assert
        assertEquals(tweetsPageRespExp, result);
    }
}

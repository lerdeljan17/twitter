package com.example.sbg.controllers;

import com.example.sbg.api.models.PostTweetReq;
import com.example.sbg.exceptions.BadRequestException;
import com.example.sbg.model.Tweet;
import com.example.sbg.services.ITweeterService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class TweeterControllerTests {


    // Create tweet successfully when all required parameters are valid
    @Test
    public void create_tweet_successfully() {
        // Arrange
        ITweeterService tweeterService = Mockito.mock(ITweeterService.class);
        TweeterController controller = new TweeterController(tweeterService);
        PostTweetReq req = new PostTweetReq("Hello World!", List.of("#test"));
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Hello World!");
        tweet.setHashtags(List.of("#test"));
        tweet.setCreatedAt(LocalDateTime.now());
        Mockito.when(tweeterService.createTweet("user", "Hello World!", List.of("#test"))).thenReturn(tweet);

        // Act
        ResponseEntity<?> response = controller.createTweet("user", req);

        // Assert
        assertEquals("", HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // Delete tweet successfully when tweet exists and user is authorized
    @Test
    public void delete_tweet_successfully() {
        // Arrange
        ITweeterService tweeterService = Mockito.mock(ITweeterService.class);
        TweeterController controller = new TweeterController(tweeterService);
        Mockito.doNothing().when(tweeterService).deleteTweet(1L, "user");

        // Act
        ResponseEntity<?> response = controller.deleteTweet("user", "1");

        // Assert
        assertEquals("", HttpStatus.OK, response.getStatusCode());
    }

    // Create tweet fails with unauthorized status when username header is missing
    @Test
    public void create_tweet_unauthorized_missing_username() {
        // Arrange
        ITweeterService tweeterService = Mockito.mock(ITweeterService.class);
        TweeterController controller = new TweeterController(tweeterService);
        PostTweetReq req = new PostTweetReq("Hello World!", List.of("#test"));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> controller.createTweet("", req));
    }

    // Delete tweet fails with unauthorized status when username header is missing
    @Test
    public void delete_tweet_unauthorized_missing_username() {
        // Arrange
        ITweeterService tweeterService = Mockito.mock(ITweeterService.class);
        TweeterController controller = new TweeterController(tweeterService);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> controller.deleteTweet("", "1"));
    }
}

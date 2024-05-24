package com.example.sbg.Controllers;

import com.example.sbg.Model.Tweet;
import com.example.sbg.Services.ITweeterService;
import com.example.sbg.api.models.Error;
import com.example.sbg.api.models.TweetsPageResp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class TweeterControllerTests {

    @Test
    public void test_return_tweets_no_filters() {

        // Arrange

        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUsername("user");
        tweet.setContent("Test tweet");
        tweet.setHashtags(Collections.singletonList("#test"));
        tweet.setCreatedAt(LocalDateTime.now());

        ITweeterService tweeterService = Mockito.mock(ITweeterService.class);
        TweeterController controller = new TweeterController(tweeterService);
        Page<Tweet> page = new PageImpl<>(Collections.singletonList(tweet));
        Mockito.when(tweeterService.getAllTweets(0, 50)).thenReturn(page);

        // Act
        ResponseEntity<?> response = controller.getTweets("user1", null, null, 50, 0);

        // Assert
        assertEquals("wrong HttpStatus code", HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(TweetsPageResp.class, response.getBody());
        TweetsPageResp resp = (TweetsPageResp) response.getBody();
        assertNotNull(resp.getTweets());
        assertFalse(resp.getTweets().isEmpty());
    }

    @Test
    public void test_unauthorized_missing_username() {
        // Arrange
        ITweeterService tweeterService = Mockito.mock(ITweeterService.class);
        TweeterController controller = new TweeterController(tweeterService);

        // Act
        ResponseEntity<?> response = controller.getTweets("", null, null, 50, 0);

        // Assert
        assertEquals("wrong HttpStatus code", HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Error.class, response.getBody());
        Error error = (Error) response.getBody();
        assertEquals("wrong HttpStatus code", 401, error.getHttpCode());
        assertEquals("wrong HttpStatus code",101, error.getErrorCode());
        assertEquals("wrong HttpStatus code","Username header is missing.", error.getMessage());
    }

}

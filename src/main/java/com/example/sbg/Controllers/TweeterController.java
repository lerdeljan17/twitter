package com.example.sbg.Controllers;

import com.example.sbg.api.models.Error;
import com.example.sbg.Model.Tweet;
import com.example.sbg.Services.ITweeterService;
import com.example.sbg.api.models.PostTweetReq;
import com.example.sbg.api.models.TweetResp;
import com.example.sbg.api.models.TweetsPageResp;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tweets")
public class TweeterController {

    private final ITweeterService tweeterService;

    public TweeterController(ITweeterService tweeterService) {
        this.tweeterService = tweeterService;
    }

    @PostMapping
    public ResponseEntity<?> createTweet(
            @RequestHeader("X-Username") String username,
            @RequestBody PostTweetReq postTweetReq) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Error(401, 101, "Username header is missing."));
        }

        Tweet tweet = tweeterService.createTweet(username, postTweetReq.getTweetBody(), postTweetReq.getHashTags());
        TweetResp tweetResp = new TweetResp(
                tweet.getId().toString(),
                tweet.getContent(),
                tweet.getHashtags(),
                tweet.getCreatedBy(),
                tweet.getCreatedAt().toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(tweetResp);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<?> deleteTweet(
            @RequestHeader("X-Username") String username,
            @PathVariable String tweetId) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Error(401, 1001, "Username header is missing."));
        }

        try {
            tweeterService.deleteTweet(Long.parseLong(tweetId), username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Error(404, 1002, "Tweet not found."));
        }
    }

    @GetMapping
    public ResponseEntity<?> getTweets(
            @RequestHeader("X-Username") String username,
            @RequestParam(value = "hashTag", required = false) List<String> hashTags,
            @RequestParam(value = "usernames", required = false) List<String> usernames,
            @RequestParam(value = "limit", defaultValue = "50") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Error(401, 101, "Username header is missing."));
        }

        if (limit < 1 || limit > 100 || offset < 0) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(new Error(412, 1003, "Limit or offset parameters are out of range."));
        }

        if (hashTags != null) {
            for (String tag : hashTags) {
                if (!tag.matches("^[a-zA-Z0-9_]*$")) {
                    return ResponseEntity.badRequest()
                            .body(new Error(400, 1004, "Invalid hashTag parameter."));
                }
            }
        }
        if (usernames != null) {
            for (String name : usernames) {
                if (!name.matches("^[a-zA-Z0-9_]*$")) {
                    return ResponseEntity.badRequest()
                            .body(new Error(400, 1005, "Invalid username parameter."));
                }
            }
        }

        Page<Tweet> tweetPage;

        if (hashTags != null && !hashTags.isEmpty() && usernames != null && !usernames.isEmpty()) {
            tweetPage = tweeterService.getTweetsByHashtagsAndUsernames(hashTags, usernames, offset, limit);
        } else if (hashTags != null && !hashTags.isEmpty()) {
            tweetPage = tweeterService.getTweetsByHashtags(hashTags, offset, limit);
        } else if (usernames != null && !usernames.isEmpty()) {
            tweetPage = tweeterService.getTweetsByUsernames(usernames, offset, limit);
        } else {
            tweetPage = tweeterService.getAllTweets(offset, limit);
        }

        List<TweetResp> tweetResponses = tweetPage.stream()
                .map(tweet -> new TweetResp(
                        tweet.getId().toString(),
                        tweet.getContent(),
                        tweet.getHashtags(),
                        tweet.getCreatedBy(),
                        tweet.getCreatedAt().toString()))
                .collect(Collectors.toList());

        TweetsPageResp tweetsPageResp = new TweetsPageResp(tweetResponses,
                tweetPage.hasNext() ? createNextPageUrl(offset, limit) : null);

        return ResponseEntity.ok(tweetsPageResp);
    }

    private String createNextPageUrl(int offset, int limit) {
        int nextPageOffset = offset + limit;
        return String.format("https://api.sb-tweets.test/v1/tweets?limit=%d&offset=%d", limit, nextPageOffset);
    }
}
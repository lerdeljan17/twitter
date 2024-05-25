package com.example.sbg.controllers;

import com.example.sbg.api.models.Error;
import com.example.sbg.api.models.PostTweetReq;
import com.example.sbg.api.models.TweetsPageResp;
import com.example.sbg.mappers.TweetMapper;
import com.example.sbg.model.Tweet;
import com.example.sbg.services.ITweeterService;
import com.example.sbg.services.rabbitMQServices.ProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweets")
public class TweeterController {

    private final ITweeterService tweeterService;
    private final ProducerService producerService;

    public TweeterController(ITweeterService tweeterService, ProducerService producerService) {
        this.tweeterService = tweeterService;
        this.producerService = producerService;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity createTweet(
            @RequestHeader("X-Username") String username,
            @RequestBody PostTweetReq postTweetReq) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Error(401, 101, "Username header is missing."));
        }

        producerService.sendTweet(postTweetReq);

        Tweet tweet = tweeterService.createTweet(username, postTweetReq.getTweetBody(), postTweetReq.getHashTags());

        var tweetResp = TweetMapper.toTweetResp(tweet);

        return ResponseEntity.status(HttpStatus.CREATED).body(tweetResp);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity deleteTweet(
            @RequestHeader("X-Username") String username,
            @PathVariable String tweetId) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Error(401, 101, "Username header is missing."));
        }

        try {
            tweeterService.deleteTweet(Long.parseLong(tweetId), username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Error(403, 102, e.getMessage()));
        }
    }

    @Operation(summary = "Get Tweets", description = "Retrieve a list of tweets based on the provided parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of tweets",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TweetsPageResp.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "412", description = "Precondition Failed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class))),
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity getTweets(
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
                    .body(new Error(412, 103, "Limit or offset parameters are out of range."));
        }

        if (hashTags != null) {
            for (String tag : hashTags) {
                if (!tag.matches("^#[a-zA-Z0-9_]*$")) {
                    return ResponseEntity.badRequest()
                            .body(new Error(400, 104, "Invalid hashTag parameter."));
                }
            }
        }
        if (usernames != null) {
            for (String name : usernames) {
                if (!name.matches("^[a-zA-Z0-9_]*$")) {
                    return ResponseEntity.badRequest()
                            .body(new Error(400, 105, "Invalid username parameter."));
                }
            }
        }

        TweetsPageResp tweetPage;

        if (hashTags != null && !hashTags.isEmpty() && usernames != null && !usernames.isEmpty()) {
            tweetPage = tweeterService.getTweetsByHashtagsAndUsernames(hashTags, usernames, offset, limit);
        } else if (hashTags != null && !hashTags.isEmpty()) {
            tweetPage = tweeterService.getTweetsByHashtags(hashTags, offset, limit);
        } else if (usernames != null && !usernames.isEmpty()) {
            tweetPage = tweeterService.getTweetsByUsernames(usernames, offset, limit);
        } else {
            tweetPage = tweeterService.getAllTweets(offset, limit);
        }

        return ResponseEntity.ok(tweetPage);
    }


}
package com.example.sbg.api.models;

import lombok.Data;

import java.util.List;

@Data
public class TweetsPageResp {
    private List<TweetResp> tweets;
    private String nextPage;

    public TweetsPageResp(List<TweetResp> tweets, String nextPage) {
        this.tweets = tweets;
        this.nextPage = nextPage;
    }

    public static String createNextPageUrl(int offset, int limit, List<String> hashtags, List<String> usernames) {
        int nextPageOffset = offset + limit;
        return String.format("http://localhost:8080/tweets?hashTag=%s&usernames=%s&limit=%d&offset=%d", hashtags, usernames, limit, nextPageOffset);
    }
}

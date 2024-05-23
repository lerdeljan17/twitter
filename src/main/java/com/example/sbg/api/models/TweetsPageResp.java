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
}

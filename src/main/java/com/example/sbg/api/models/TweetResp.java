package com.example.sbg.api.models;

import lombok.Data;

import java.util.List;

@Data
public class TweetResp {
    private String tweetId;
    private String tweetBody;
    private List<String> hashTags;
    private String createdBy;
    private String createdAt;

    public TweetResp(String tweetId, String tweetBody, List<String> hashTags, String createdBy, String createdAt) {
        this.tweetId = tweetId;
        this.tweetBody = tweetBody;
        this.hashTags = hashTags;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}

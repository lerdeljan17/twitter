package com.example.sbg.api.models;

import lombok.Data;

import java.util.List;

@Data
public class PostTweetReq {
    private String tweetBody;
    private List<String> hashTags;

    public PostTweetReq(String tweetBody, List<String> hashTags) {
        this.tweetBody = tweetBody;
        this.hashTags = hashTags;
    }
}
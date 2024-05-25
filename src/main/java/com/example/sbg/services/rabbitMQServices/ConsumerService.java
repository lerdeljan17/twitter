package com.example.sbg.services.rabbitMQServices;

import com.example.sbg.api.models.PostTweetReq;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @RabbitListener(queues = "tweetQueue")
    public void receiveTweet(PostTweetReq message) {
        // Process the received message
        System.out.println("Received tweet: " + message);
        // Add logic to process the tweet
    }
}

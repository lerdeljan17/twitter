package com.example.sbg.services.rabbitMQServices;

import com.example.sbg.api.models.PostTweetReq;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTweet(PostTweetReq postTweetReq) {
        rabbitTemplate.convertAndSend("tweetQueue", postTweetReq);
    }

    public void sendReadRequest(String request) {
        rabbitTemplate.convertAndSend("readTweetQueue", request);
    }
}

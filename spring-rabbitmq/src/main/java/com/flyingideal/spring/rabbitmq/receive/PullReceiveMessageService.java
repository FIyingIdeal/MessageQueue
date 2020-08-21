package com.flyingideal.spring.rabbitmq.receive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * 通过 {@link org.springframework.amqp.rabbit.core.RabbitTemplate#receive(String)} 及其重载方法拉取数据
 * 注意： 在拉模式上中 Web 页面上无法看到此 Consumer，每次可以拉取一条数据
 *
 *
 * Listener 是 RabbitMQ 推模式，在 Web 页面上的 Consumers 中可以看到通过 Listener 注册的消费者
 *
 * @author yanchao
 * @date 2020-08-21 19:54
 */
@Slf4j
@Service
public class PullReceiveMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Object receiveMessage() {
        String queueName = "queue.normal";
        Object message = this.rabbitTemplate.receiveAndConvert(queueName);
        log.info("received message from {} is {}", queueName, message);
        return message;
    }
}

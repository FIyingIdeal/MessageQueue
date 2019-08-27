package com.flyingideal.spring.rabbitmq.sender;

import com.flyingideal.spring.rabbitmq.BaseTest;
import com.flyingideal.spring.rabbitmq.config.RabbitMQConstant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yanchao
 * @date 2019-08-23 14:52
 */
public class DirectExchangeSenderTest extends BaseTest {

    @Autowired
    private DirectExchangeSender directExchangeSender;

    @Test
    public void sendMessage() {
        String message = "Hello Rabbit direct exchange!";
        // directExchangeSender.sendMessage(message);
        directExchangeSender.sendMessage(RabbitMQConstant.DIRECT_EXCHANGE_NAME, "", message);
    }

    @Test
    public void sendMessageWithDefaultExchangeAndRoutingKey() {
        directExchangeSender.sendMessageWithDefaultExchangeAndRoutingKey("sendMessageWithDefaultExchangeAndRoutingKey");
    }
}

package com.flyingideal.spring.rabbitmq.job;

import com.flyingideal.spring.rabbitmq.producer.DirectExchangeSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author yanchao
 * @date 2019-08-23 15:00
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "learn.rabbitmq", name = "consumer.error", havingValue = "false", matchIfMissing = true)
public class MQSenderScheduleJob {

    @Autowired
    private DirectExchangeSender directExchangeSender;

    @Scheduled(fixedRate = 3000)
    public void directExchangeSender() {
        log.info("Ready to send message!");
        String message = "Hello Rabbit direct exchange!";
        directExchangeSender.sendMessage(message);
    }


}

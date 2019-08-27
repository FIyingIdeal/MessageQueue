package com.flyingideal.spring.rabbitmq.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author yanchao
 * @date 2019-08-23 18:22
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "learn.rabbitmq", name = "consumer.error", havingValue = "true")
public class ErrorDirectExchangeSender {

    @Autowired
    private DirectExchangeSender directExchangeSender;

    @Scheduled(initialDelay = 1000, fixedRate = 3600 * 1000)
    public void directExchangeErrorSender() {

        for (int i = 1; i < 7; i++) {
            String message = "Hello Rabbit direct exchange " + i;
            log.info("Ready to send message!");
            if (i % 4 == 0) {
                log.info("Ready to send error message!");
                message = "error" + LocalDateTime.now();
            }
            directExchangeSender.sendMessage(message);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
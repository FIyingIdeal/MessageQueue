package com.flyingideal.spring.rabbitmq.listener;


import com.flyingideal.spring.rabbitmq.config.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanchao
 * @date 2019-08-22 18:17
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "learn.rabbitmq", name = "consumer.error", havingValue = "false")
@RabbitListener(queues = RabbitMQConstant.DIRECT_QUEUE_NAME)
public class DirectQueueListener {

    /**
     * 如果不指定 messageConverter 的话，方法参数类型必须是 byte[] 类型，否则将会报错
     * @param message   消息体
     */
    // @RabbitHandler
    public void directQueueConsumer(byte[] message) {
        log.info("======= directQueueConsumer(byte[]) receive message : [{}]", new String(message));
    }

    /**
     * 如果指定 content_type 为 text/plain ，则会调用这个 Handler
     * @param message   消息体
     */
    @RabbitHandler
    public void directQueueConsumer(String message) {
        log.info("======== directQueueConsumer(String) receive message : [{}]", message);
    }


}

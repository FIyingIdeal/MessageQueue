package com.flyingideal.spring.rabbitmq.listener;

import com.flyingideal.spring.rabbitmq.config.RabbitMQConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author yanchao
 * @date 2019-09-09 23:27
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = {"simple.acknowledge-mode", "direct.acknowledge-mode"}, havingValue = "manual")
public class DirectQueueListenerWithManualAck {

    @RabbitListener(queues = RabbitMQConstant.DIRECT_QUEUE_NAME)
    public void getDirectMessageWithManualAck(String content, Channel channel, Message message) throws IOException {
        try {
            log.info("direct message with manual ack : {}", content);
            // 如果抛出异常，则会一直重试；如果抛出 Error 的话，则抛出 Error 消费者线程会退出，其他线程不受影响
            if (content.contains("manualack")) {
                // throw new IllegalArgumentException("手动消息确认异常测试");
                throw new OutOfMemoryError();
            }
            TimeUnit.SECONDS.sleep(10);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error: direct message with manual ack consume error :{}, {}", e.getMessage(),
                    message.getMessageProperties().getDeliveryTag());
            // basicNack ，可以拒绝一条或多条消息，第二个参数如果为 false，则只拒绝一条消息，与 basicReject 一样。
            // 如果为 true，表示拒绝 deliveryTag 之前所有未被当前消费者确认的消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            // basicReject 拒绝一条消息，如果第二个参数 requeue 设置为 true，则消息会重新入队，若为false，且有死信队列，将会进入死信队列，否则将会丢弃
            // channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);

        }
    }
}

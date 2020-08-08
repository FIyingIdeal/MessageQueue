package com.flyingideal.spring.rabbitmq.listener;

import com.flyingideal.spring.rabbitmq.config.RabbitMQConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author yanchao
 * @date 2019-08-23 11:53
 */
@Configuration
/*@ConditionalOnExpression("${learn.rabbitmq.consumer.error:false} == false " +
        "&& ('${spring.rabbitmq.listener.simple.acknowledge-mode}'.equals('auto') " +
        "|| '${spring.rabbitmq.listener.direct.acknowledge-mode}'.equals('auto'))")*/
@ConditionalOnProperty(prefix = "learn.rabbitmq", name = "consumer.error", havingValue = "false")
@Slf4j
@RabbitListener(queues = RabbitMQConstant.DIRECT_QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
public class DirectQueueContainerFactoryListener {

    @Autowired
    private Environment environment;

    /**
     * 注意 {@link @RabbitListener} 里配置了 containerFactory 属性， 属性值是 autoConfig 中默认 bean 的名称
     * {@link com.flyingideal.spring.rabbitmq.config.RabbitMQConfig#simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer, ConnectionFactory)}
     * 设置了 MessageConverter 为处理 json 的 Converter。如果要处理 json，消息的 content_type 必须为 application/json。
     *
     * 但需要注意，如果同时配置了多个 RabbitListener，相当于配置了多个消费者， RabbitMQ 会将消息依次投递给这些消费者，类似 load balance
     * @param message   消息体
     */
    @RabbitHandler
    public void directQueueJsonConsumer(String content, Channel channel, Message message) throws Exception {
        log.info(">>> directQueueJsonConsumer(String) receive message : [{}]", content);
        try {
            if ("manual".equals(environment.getProperty("spring.rabbitmq.listener.simple.acknowledge-mode"))) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            log.error("{}", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }

    }
}

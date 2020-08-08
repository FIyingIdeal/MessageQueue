package com.flyingideal.spring.rabbitmq.producer;

import com.flyingideal.spring.rabbitmq.config.RabbitMQConstant;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yanchao
 * @date 2019-08-23 12:03
 */
@Component
public class DirectExchangeSender {

    /**
     * {@link AmqpAdmin} 可用来动态创建 exchange， queue，Binding 等信息
     */
    private final AmqpAdmin amqpAdmin;
    /**
     * 通过构造方法注入。{@link AmqpTemplate} 是 {@link RabbitTemplate} 的父接口，这里注入的其实还是 RabbitTemplate 实例
     */
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public DirectExchangeSender(AmqpAdmin amqpAdmin, AmqpTemplate amqpTemplate) {
        this.amqpAdmin = amqpAdmin;
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * 发送消息的时候如果不指定 exchange 与 binding key，会使用 RabbitMQ 提供的默认交换机及默认的 binding key 来投递消息
     * 这两个默认值都为 ""
     * 注意，队列名称不允许为空，但 binding key 允许为 ""
     * @param message   消息体
     */
    public void sendMessageWithDefaultExchangeAndRoutingKey(Object message) {
        this.amqpTemplate.convertAndSend(message);
    }

    /**
     * 如果不指定 exchange 的话，会使用 RabbitMQ 提供的一个默认交换机，它是一个 direct 类型的交换机
     * @param message   消息体
     */
    public void sendMessageWithDefaultExchange(Object message) {
        this.amqpTemplate.convertAndSend(RabbitMQConstant.DIRECT_BINDING, message);
    }

    /**
     * 将消息发送到指定 exchange（通过 exchange name 匹配），exchange 按照 routing key 将消息发送到与其绑定的指定队列
     * 如果 exchange 与 队列 的 binding key 与发送消息的 routing key 不匹配，则消息通过 exchange 后就丢失了
     * @param message   消息体
     */
    public void sendMessage(Object message) {
        this.amqpTemplate.convertAndSend(RabbitMQConstant.DIRECT_EXCHANGE_NAME,
                RabbitMQConstant.DIRECT_BINDING, message);
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        this.amqpTemplate.convertAndSend(exchange, routingKey, message);
    }


    public void sendTtlMessage(Object message, long ttl) {
        this.sendTtlMessage(RabbitMQConstant.DIRECT_EXCHANGE_NAME, RabbitMQConstant.DIRECT_BINDING, message, ttl);
    }

    /**
     * 发送带 ttl 的消息
     */
    public void sendTtlMessage(String exchange, String routingKey, Object message, long ttl) {
        this.amqpTemplate.convertAndSend(exchange, routingKey, message, m -> {
            m.getMessageProperties().setExpiration(String.valueOf(ttl));
            return m;
        });
    }
}

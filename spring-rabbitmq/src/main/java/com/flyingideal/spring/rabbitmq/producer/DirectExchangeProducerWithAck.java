package com.flyingideal.spring.rabbitmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 生产发送消息确认机制
 * @author yanchao
 * @date 2019-08-28 21:30
 * @link <a>https://www.jianshu.com/p/fae8fca98522</a>
 */
@Slf4j
@Component
public class DirectExchangeProducerWithAck
        implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 这个方法只确认消息是否到达 exchange ，是否到达以方法中的 ack 参数为准，true 表示到达 exchange，否则进入黑洞...
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info(">>> 消息发送成功 : {}", correlationData);
        } else {
            log.error(">>> 消息发送失败：{}", cause);
        }
    }

    /**
     * 当消息 <b>没有</b> 正确到达队列的时候回调这个方法，如果正确到达队列则不回调这个方法
     * @param message       消息内容
     * @param replyCode
     * @param replyText
     * @param exchange      交换器
     * @param routingKey    路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

    }
}

package com.flyingideal.spring.rabbitmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 生产者发送消息确认机制
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

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        String publisherConfirmsKey = "spring.rabbitmq.publisher-confirms";
        String publisherReturnsKey = "spring.rabbitmq.publisher-returns";
        if (environment.getProperty(publisherConfirmsKey, Boolean.class, Boolean.FALSE)) {
            rabbitTemplate.setConfirmCallback(this);
        }
        if (environment.getProperty(publisherReturnsKey, Boolean.class, Boolean.FALSE)) {
            rabbitTemplate.setReturnCallback(this);
        }
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
            log.info(">>> 消息到达 exchange : {}", correlationData);
        } else {
            log.error(">>> 消息未到达 exchange : {}", cause);
            // TODO 消息投递失败，尝试重新投递，确保消息不丢失
        }
    }

    /**
     * 当消息 <b>没有</b> 正确到达队列的时候回调这个方法，如果正确到达队列则不回调这个方法
     * 没有正确到达队列的两种情况：
     *      1. exchange 未与存在的队列通过指定的 routing-key 进行绑定；
     *      2. 队列不存在；
     *
     * 另外，需要注意的是，如果发送消息给一个设置了备份交换机的交换机，在消息无法被路由到队列时，消息会被路由到备份交换机，
     * 此时 returnedMessage 将不会生效
     * @param message       消息内容
     * @param replyCode     应答码
     * @param replyText     消息投递错误描述，如 NO_ROUTE
     * @param exchange      交换器
     * @param routingKey    路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息未正确到达队列！消息主体 : {}, 应答码：{}, 描述：{}, 交换器：{}, 路由键：{}",
                message, replyCode, replyText, exchange, routingKey);
        // TODO 消息投递失败，尝试从新投递，确保消息不丢失
    }
}

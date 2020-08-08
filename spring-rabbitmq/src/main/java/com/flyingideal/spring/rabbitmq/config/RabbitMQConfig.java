package com.flyingideal.spring.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanchao
 * @date 2019-08-22 17:46
 */
@Configuration
public class RabbitMQConfig {

    private boolean exchangeDurable = false;
    private boolean exchangeAutoDelete = false;

    private boolean queueDurable = false;
    private boolean queueExclusive = false;
    private boolean queueAutoDelete = false;

    /**
     * {@link DirectExchange} 参数：
     *      durable : 是否持久化，仅仅是 Exchange 信息的持久化，在 RabbitMQ 重起时会自动重建；
     *      autoDelete : 是否自动删除。自动删除的前提是至少有一个队列或交换器与之绑定，之后断开所有与之关联的 Queue
     */
    @Bean("directExchange")
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitMQConstant.DIRECT_EXCHANGE_NAME, exchangeDurable, exchangeAutoDelete);
    }

    /**
     * {@link Queue} 参数：
     *      name : queue 名称
     *      durable : 是否持久化，仅仅是Queue源信息的持久化
     *      exclusive : 是否排他，如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除。
     *              - 排他队列是基于连接（Connection）可见的，同一个连接的不同信道（Channel）是可以同时访问同一个连接创建的排他队列的；
     *              - "首次"是指一个连接声明了一个排他队列，则其他连接不允许建立同名的排他队列，否则报错；
     *              - 即使排他队列是持久化的，一旦连接关闭或客户端退出，则排他队列也会自动删除。
     *      autoDelete : 是否自动删除。自动删除的前提是至少有一个消费者连接到这个队列。之后所有与这个队列连接的消费者都断开连接时才会自动删除，
     *                  因为要考虑刚创建好，还没有消费者连接的情况，不然刚创建好就会被删除，永远无法创建了
     * @return
     */
    @Bean("directQueue")
    @ConditionalOnProperty(prefix = "learn.rabbitmq.queue", name = "auto-create", havingValue = "true", matchIfMissing = true)
    public Queue directQueue() {
        return new Queue(RabbitMQConstant.DIRECT_QUEUE_NAME, queueDurable, queueExclusive, queueAutoDelete);
    }

    @Bean("directBinding")
    @ConditionalOnProperty(prefix = "learn.rabbitmq.queue", name = "auto-create", havingValue = "true", matchIfMissing = true)
    public Binding directBinding(@Qualifier("directExchange") DirectExchange directExchange,
                                 @Qualifier("directQueue") Queue directQueue ) {
        return BindingBuilder.bind(directQueue).to(directExchange).with(RabbitMQConstant.DIRECT_BINDING);
    }

    // ====================== 备份交换机 ========================

    /**
     * 声明交换机的时候设置备份交换机。当消息无法被路由到任何一个队列，且没有设置 mandatory 参数
     *
     * 设置了 mandatory 参数为 true，消息在无法路由到队列时，RabbitMQ 会调用 Basic.Return 命令将消息返回给生产者，否则直接丢弃
     * Spring 通过 {@link org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback} 封装了该行为。
     * {@link com.flyingideal.spring.rabbitmq.producer.DirectExchangeProducerWithAck#returnedMessage}
     *
     * 如果同时设置了备份交换机和 mandatory 参数，则 mandatory 参数行为（回调returnedMessage方法）将会被忽略
     */
    @Bean("directExchangeWithAlternateExchange")
    public DirectExchange directExchangeWithAlternateExchange() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("alternate-exchange", "ae");
        return new DirectExchange("dewae", false, false, map);
    }

    @Bean("alternateExchange")
    public FanoutExchange alternateExchange() {
        return new FanoutExchange("ae", exchangeDurable, exchangeAutoDelete);
    }

    @Bean("alternateExchangeQueue")
    public Queue alternateExchangeQueue() {
        return new Queue("alternateQueue", false, false, false);
    }

    /**
     * 备份交换机与队列绑定
     * @param fanoutExchange            备份交换机，是一个 fanout 类型的交换机
     * @param alternateExchangeQueue    备份交换机绑定的队列
     */
    @Bean("alternateQueueBindExchange")
    public Binding alternateQueueBindExchange(
            @Qualifier("alternateExchange") FanoutExchange fanoutExchange,
            @Qualifier("alternateExchangeQueue") Queue alternateExchangeQueue) {
        return BindingBuilder.bind(alternateExchangeQueue).to(fanoutExchange);
    }

    /**
     * 备份交换机 与 Direct 类型交换机绑定
     * @param directExchange        direct 交换机
     * @param alternateExchange     备份交换机
     */
    @Bean("alternateExchangeBindDirectExchange")
    public Binding alternateExchangeBindDirectExchange(
            @Qualifier("directExchangeWithAlternateExchange") DirectExchange directExchange,
            @Qualifier("alternateExchange") FanoutExchange alternateExchange) {
        return BindingBuilder.bind(alternateExchange).to(directExchange).with("");
    }

    //=========================================================

    @Bean
    public TopicExchange topicExchange() {
        // Exchange名称，是否持久化，是否删除
        return new TopicExchange(RabbitMQConstant.TOPIC_EXCHANGE_NAME, exchangeDurable, exchangeAutoDelete);
    }

    @Bean("topicQueue")
    public Queue topicQueue() {
        return new Queue(RabbitMQConstant.TOPIC_QUEUE_NAME, queueDurable, queueExclusive, queueAutoDelete);
    }

    @Bean
    @Qualifier("topicBinding")
    public Binding topicBinding(TopicExchange topicExchange,
                                @Qualifier("topicQueue") Queue topicQueue) {
        return BindingBuilder.bind(topicQueue).to(topicExchange).with(RabbitMQConstant.TOPIC_BINDING);
    }

    /**
     * 自定义的 {@link SimpleRabbitListenerContainerFactory}，主要是为了设置 {@link MessageConverter}
     * 并非必须通过这种方式来设置一个 {@link MessageConverter}，因为这样只是为消费者设置了相应的 {@link MessageConverter}，
     * 而发消息的话同样需要为 {@link AmqpTemplate} 设置 {@link MessageConverter}。
     * 通过如下 {@link this#jackson2JsonMessageConverter()} 统一设置 {@link MessageConverter}
     * 其通过 {@link org.springframework.beans.factory.ObjectProvider} 将 MessageConverter Bean 注入到依赖类中
     *
     * 在 autoconfig 中如果没有配置的话会自动配置一个
     * {@link org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration#simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer, ConnectionFactory)}
     *
     */
    // @Bean("rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        // 自定义 SimpleRabbitListenerContainerFactory
        // SimpleRabbitListenerContainerFactory myFactory = new MySimpleRabbitListenerContainerFactory();
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}

package com.flyingideal.spring.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitMQConstant.DIRECT_EXCHANGE_NAME, exchangeDurable, exchangeAutoDelete);
    }

    @Bean("directQueue")
    public Queue directQueue() {
        return new Queue(RabbitMQConstant.DIRECT_QUEUE_NAME, queueDurable, queueExclusive, queueAutoDelete);
    }

    @Bean("directBinding")
    public Binding directBinding(DirectExchange directExchange,
                                 @Qualifier("directQueue") Queue directQueue ) {
        return BindingBuilder.bind(directQueue).to(directExchange).with(RabbitMQConstant.DIRECT_BINDING);
    }

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

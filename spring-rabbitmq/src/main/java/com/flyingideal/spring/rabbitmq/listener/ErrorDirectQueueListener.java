package com.flyingideal.spring.rabbitmq.listener;

import com.flyingideal.spring.rabbitmq.config.RabbitMQConstant;
import com.flyingideal.spring.rabbitmq.override.MySimpleRabbitListenerContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * 测试消费消息的时候出现异常的情况。
 * 当抛出 {@link Error} 的时候，会将当前消费者相关的所有线程都下线，且抛出异常线程所处理的消息会重新入队，队列 Ready 状态消息加 1，其他线程正在处理的消息会继续消费完毕，但不会再接收任何消息而是下线
 * 当抛出 {@link Exception} 的时候，消费者线程不会下线，但会一直尝试处理这个消息，如果一直有异常，一直重试，且消息状态为 Unacked
 *
 * @author yanchao
 * @date 2019-08-23 17:56
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "learn.rabbitmq", name = "consumer.error", havingValue = "true")
public class ErrorDirectQueueListener {

    private static int counter = 0;
    private Random random = new Random(47);

    /**
     * 自定义 {@link MySimpleRabbitListenerContainerFactory}
     */
    // @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new MySimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    /**
     * {@link RabbitListener#concurrency()} 设置的时候可以使用 m-n 形式。m 表示并发线程数量初始值，n 表示并发线程最大值
     * @param content   消息体
     */
    @RabbitListener(queues = RabbitMQConstant.DIRECT_QUEUE_NAME, concurrency = "3-5")
    public void errorConsumer(String content) {
        log.info(">>> Thread {} receive message : {}", Thread.currentThread().getName(), content);
        String errorMessage = "error";
        if (content.contains(errorMessage) && counter++ < 1) {
            // 当消息内容是 error 的时候，抛出一个 error 进行测试
            throw new Error("rabbitmq error 测试");
            // throw new RuntimeException("rabbitmq exception 测试");
        }

        try {
            // 模拟消费时间
            TimeUnit.SECONDS.sleep(7);
            // 每个消费者线程消费完消息随机休眠一个时间
            int randomSleepSeconds = random.nextInt(4);
            log.info("Thread {} will sleep {} s", Thread.currentThread().getName(), randomSleepSeconds);
            TimeUnit.SECONDS.sleep(randomSleepSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(">>>>> Thread {} consume message [{}] done!", Thread.currentThread().getName(), content);
    }
}

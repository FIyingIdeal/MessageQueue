package com.flyingideal.spring.rabbitmq.rewrite;

import com.flyingideal.spring.rabbitmq.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;

/**
 * 测试自定义 {@link SimpleRabbitListenerContainerFactory}
 *
 * 配置方式：
 * 修改 {@link RabbitMQConfig#simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer, ConnectionFactory)}
 * 中定义的 {@link SimpleRabbitListenerContainerFactory} 的类型，并将 {@code @Bean} 注释放开
 *
 * @author yanchao
 * @date 2019-08-26 18:41
 */
public class MySimpleRabbitListenerContainerFactory extends SimpleRabbitListenerContainerFactory {

    /**
     * 重写 {@link SimpleRabbitListenerContainerFactory#createContainerInstance()}
     * 修改 {@link SimpleMessageListenerContainer#forceCloseChannel} 的属性值为false。
     * SpringBoot 并没有为这个参数提供类似其他参数的配置修改方式，如
     * {@link SimpleRabbitListenerContainerFactory#initializeContainer(SimpleMessageListenerContainer, RabbitListenerEndpoint)}
     * 和 {@link AbstractRabbitListenerContainerFactory#createListenerContainer(RabbitListenerEndpoint)}。
     * 这几个类中并没有提供修改 {@code forceCloseChannel} 的方法调用，只能通过重写 {@code createContainerInstance()} 方法来实现，
     * 因为这几个配置类都是基于这个方法返回值进行参数设置的。
     *
     * {@link SimpleMessageListenerContainer#forceCloseChannel} 这个参数
     */
    @Override
    protected SimpleMessageListenerContainer createContainerInstance() {
        SimpleMessageListenerContainer container = super.createContainerInstance();
        // 设置 forceCloseChannel 为 false，即不自动关闭 channel
        container.setForceCloseChannel(false);
        return container;
    }
}

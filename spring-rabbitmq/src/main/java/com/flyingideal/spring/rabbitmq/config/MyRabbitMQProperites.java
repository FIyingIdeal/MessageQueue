package com.flyingideal.spring.rabbitmq.config;

/**
 * @author yanchao
 * @date 2019-08-23 17:04
 */
// @ConfigurationProperties(prefix = "learn")
public class MyRabbitMQProperites {

    private Consumer consumer;

    public Consumer getConsumer() {
        return consumer;
    }

    public static class Consumer {
        private boolean errorTest = false;

        public boolean isErrorTest() {
            return errorTest;
        }
    }

}

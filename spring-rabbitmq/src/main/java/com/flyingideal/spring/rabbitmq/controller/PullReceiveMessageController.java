package com.flyingideal.spring.rabbitmq.controller;

import com.flyingideal.spring.rabbitmq.receive.PullReceiveMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通过 {@link org.springframework.amqp.rabbit.core.RabbitTemplate#receive(String)} 方法拉数据测试
 * @author yanchao
 * @date 2020-08-21 20:05
 */
@RestController
@RequestMapping("pull")
public class PullReceiveMessageController {

    @Autowired
    private PullReceiveMessageService pullReceiveMessageService;

    @GetMapping("/receive")
    public Object receiveMessage() {
        return this.pullReceiveMessageService.receiveMessage();
    }
}

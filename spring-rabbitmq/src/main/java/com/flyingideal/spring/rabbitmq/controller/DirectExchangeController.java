package com.flyingideal.spring.rabbitmq.controller;

import com.flyingideal.spring.rabbitmq.producer.DirectExchangeSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author yanchao
 * @date 2020-08-07 23:56
 */
@RestController
@RequestMapping("/direct")
public class DirectExchangeController {

    @Autowired
    private DirectExchangeSender directExchangeSender;


    @GetMapping("/message")
    public String sendMessage2DirectExchange() {
        String message = "direct exchange message";
        this.directExchangeSender.sendMessage(message);
        return message;
    }


    @GetMapping("/message/{exchange}/{key}")
    public String sendMessage2DefineExchange(@PathVariable String exchange,
                                           @PathVariable String key,
                                           @RequestParam(value = "message", required = false,
                                                   defaultValue = "direct exchange message") String message) {
        this.directExchangeSender.sendMessage(exchange, key, message);
        return message;
    }

    @GetMapping("/message/ttl")
    public String sendTtlMessage(@RequestParam("ttl") long ttl) {
        String message = "direct exchange ttl message";
        this.directExchangeSender.sendTtlMessage(message, ttl);
        return message;
    }

    @GetMapping("/message/ttl/exchange")
    public String sendTtlMessageWithExchange(@RequestParam("ttl") long ttl,
                                             @RequestParam("exchange") String exchange,
                                             @RequestParam("routingKey") String routingKey,
                                             @RequestParam("message") String message) {
        this.directExchangeSender.sendTtlMessage(exchange, routingKey, message, ttl);
        return message;
    }
}

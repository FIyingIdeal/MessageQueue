## 消费者消息确认机制
SpringBoot 集成 RabbitMQ 确认机制有三种：none、auto(默认)、manual。

### none



### auto

如果用户不配置消费者消息确认机制，则默认为 `auto`，SpringBoot 会自动进行消息确认或消息拒绝，以及拒绝后直接丢弃或重新入队，具体规则如下：

1. 如果消息成功被消费（即消费过程中没有抛出异常），则自动确认；
2. 当抛出 `AmqpRejectAndDontRequeueException` 异常时，消息会被拒绝，且 `requeue = false`，不重新入队；
3. 当抛出 `ImmediateAcknowledgeAmqpExcepton` 异常时，消费则会被确认；
4. 当抛出其他异常时，消息会被拒绝，且 `requeue = true`，此时会发生死循环，可以通过设置 `setDefaultRequeueReject（默认值为true）` 抛弃消息；
 
### manual
 
消费者消息确认机制为 `manual` 时，用户需要通过调用指定方法进行手动消息确认或消息拒绝。如果不对消息作出应答，RabbitMQ 会认为当前队列没有消费完成
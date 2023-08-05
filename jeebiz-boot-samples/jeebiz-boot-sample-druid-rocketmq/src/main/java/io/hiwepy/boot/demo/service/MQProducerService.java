package io.hiwepy.boot.demo.service;

import io.hiwepy.boot.demo.web.dto.MessageDTO;
import org.apache.rocketmq.client.producer.SendResult;

public interface MQProducerService {

    /**
     * 普通发送（这里的参数对象可以随意定义，可以发送个对象，也可以是字符串等）
     */
    void send(MessageDTO message);

    /**
     * 发送同步消息（阻塞当前线程，等待broker响应发送结果，这样不太容易丢失消息）
     * （msgBody也可以是对象，sendResult为返回的发送结果）
     */
    SendResult sendMsg(String msgBody);

    /**
     * 发送异步消息（通过线程池执行发送到broker的消息任务，执行完后回调：在SendCallback中可处理相关成功失败时的逻辑）
     * （适合对响应时间敏感的业务场景）
     */
    void sendAsyncMsg(String msgBody);

    /**
     * 发送延时消息（上面的发送同步消息，delayLevel的值就为0，因为不延时）
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    void sendDelayMsg(String msgBody, int delayLevel);

    /**
     * 发送单向消息（只负责发送消息，不等待应答，不关心发送结果，如日志）
     */
    void sendOneWayMsg(String msgBody);

    /**
     * 发送带tag的消息，直接在topic后面加上":tag"
     */
    SendResult sendTagMsg(String msgBody);

}

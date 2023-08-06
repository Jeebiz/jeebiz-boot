package io.hiwepy.boot.sample.web.mvc;

import io.hiwepy.boot.api.ApiRestResponse;
import io.hiwepy.boot.sample.service.MQProducerService;
import io.hiwepy.boot.sample.web.dto.MessageDTO;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {

    @Autowired
    private MQProducerService mqProducerService;

    @GetMapping("/send")
    public void send() {
        MessageDTO message = new MessageDTO();
        mqProducerService.send(message);
    }

    @GetMapping("/sendTag")
    public ApiRestResponse<SendResult> sendTag() {
        SendResult sendResult = mqProducerService.sendTagMsg("带有tag的字符消息");
        return ApiRestResponse.success(sendResult);
    }

}
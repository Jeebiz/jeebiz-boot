package io.hiwepy.boot.demo.setup.rocketmq;

import java.util.Date;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class Producer implements CommandLineRunner {

	@Autowired(required = false)
	protected RocketMQTemplate rocketmqTemplate;
	
	@Scheduled(fixedDelay = 3000) // 每3s执行1次
	public void send() throws Exception {
		
		/*// send queue.
		
		MessageQueue mq = new MessageQueue("TEST", "brokerName", 1);
		Message msg = new Message("TEST", // topic
				"TEST", // tag
				"KKK", // key用于标识业务的唯一性
				"hi,RocketMQ".getBytes()// body 二进制字节数组
		);

		SendResult result = rocketmqTemplate.send(msg, mq);
		System.out.println(result);*/
		
		// send topic.
		Message msg2 = new Message("TopicA", // topic
				"TagA", // tag
				"KKK", // key用于标识业务的唯一性； key 消息关键词，多个Key用KEY_SEPARATOR隔开（查询消息使用）
				(new Date() + "hi,RocketMQ(topic)").getBytes()// body 二进制字节数组
		);
		
		rocketmqTemplate.asyncSend("destination", msg2 ,new SendCallback(){

			@Override
			public void onSuccess(SendResult sendResult) {
				
			}

			@Override
			public void onException(Throwable e) {
				
			}
    		
    	});
		//System.out.println(result2);

	}
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Message was sent to the Queue");
	}

}
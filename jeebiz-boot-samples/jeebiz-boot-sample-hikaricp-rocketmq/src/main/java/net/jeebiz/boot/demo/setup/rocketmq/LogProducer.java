package net.jeebiz.boot.demo.setup.rocketmq;

import java.util.Date;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LogProducer implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogProducer.class);

	@Autowired(required = false)
	protected RocketMQTemplate rocketmqTemplate;

	public void send(String text) throws Exception {

		Message msg = new Message("TopicA", // topic
				"TagA", // tag
				"KKK", // key用于标识业务的唯一性； key 消息关键词，多个Key用KEY_SEPARATOR隔开（查询消息使用）
				(new Date() + text).getBytes() // body 二进制字节数组
		);
		rocketmqTemplate.asyncSend("destination", msg ,new SendCallback(){

			@Override
			public void onSuccess(SendResult sendResult) {
				
			}

			@Override
			public void onException(Throwable e) {
				
			}
    		
    	});

	}

	@Override
	public void run(String... strings) throws Exception {
		send("This is a log message.");
		LOGGER.info("Log Message was sent to the Queue named sample.log");
	}

}
package net.jeebiz.boot.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.jms.JMSException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.test.context.junit4.SpringRunner;

import net.jeebiz.boot.demo.setup.Producer;

/**
 * Integration tests for demo application.
 *
 * @author Eddú Meléndez
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleActiveMqTests {

	@Rule
	public OutputCaptureRule outputCapture = new OutputCaptureRule();

	@Autowired
	private Producer producer;

	@Test
	public void sendSimpleMessage() throws InterruptedException, JMSException {
		this.producer.send("Test message");
		Thread.sleep(1000L);
		assertThat(this.outputCapture.toString().contains("Test message")).isTrue();
	}

}
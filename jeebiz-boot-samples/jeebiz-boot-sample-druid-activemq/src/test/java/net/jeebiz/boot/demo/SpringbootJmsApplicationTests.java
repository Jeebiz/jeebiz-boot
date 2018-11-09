package net.jeebiz.boot.demo;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import net.jeebiz.boot.demo.setup.Producer;  
  
@RunWith(SpringRunner.class)  
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringbootJmsApplicationTests {  
      
    @Autowired  
    private Producer producer;  
      
    @Test  
    public void contextLoads() throws InterruptedException {  
        Destination destination = new ActiveMQQueue("mytest.queue");  
          
        for(int i=0; i<100; i++){  
            producer.sendMessage(destination, "myname is chhliu!!!");  
        }  
    }  
  
}  

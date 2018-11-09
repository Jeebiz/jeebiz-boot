package net.jeebiz.boot.demo.web.mvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.jeebiz.boot.demo.setup.rocketmq.LogProducer;

@RestController
public class RocketMQController {
	
    @Autowired  
    private LogProducer logProducer;  
    
    @GetMapping("/rocketmq/send")  
    public String activemq(HttpServletRequest request, String msg) {  
        msg = StringUtils.isEmpty(msg) ? "This is Empty Msg." : msg;  
      
        try {  
            logProducer.send(msg);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return "Rocketmq has sent OK.";  
    }  
    
    
}

package io.hiwepy.boot.demo.web.mvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hiwepy.boot.demo.setup.LogProducer;

@RestController
public class ActiveMQController {

    @Autowired
    private LogProducer logProducer;

    @GetMapping("/activemq/send")
    public String activemq(HttpServletRequest request, String msg) {
        msg = StringUtils.isEmpty(msg) ? "This is Empty Msg." : msg;

        try {
            logProducer.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Activemq has sent OK.";
    }


}

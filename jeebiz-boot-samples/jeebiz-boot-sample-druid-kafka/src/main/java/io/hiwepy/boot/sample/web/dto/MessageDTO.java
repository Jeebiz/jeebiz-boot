package io.hiwepy.boot.sample.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageDTO {

    private String topic;
    private String tag;
    private String key;
    private String body;

}

/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ExternalProperties.PREFIX)
@Data
public class ExternalProperties {

    public static final String PREFIX = "external";

    private String baiduAk;

}

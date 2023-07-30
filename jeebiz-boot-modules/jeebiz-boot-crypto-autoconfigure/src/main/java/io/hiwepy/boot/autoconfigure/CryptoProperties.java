/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(CryptoProperties.PREFIX)
@Data
public class CryptoProperties {

    public static final String PREFIX = "crypto";

    /**
     * 模式
     * 加密算法模式，是用来描述加密算法（此处特指分组密码，不包括流密码，）在加密时对明文分组的模式，它代表了不同的分组方式
     */
    private Mode mode;

    /**
     * 补码方式：
     * 补码方式是在分组密码中，当明文长度不是分组长度的整数倍时，需要在最后一个分组中填充一些数据使其凑满一个分组的长度。
     */
    private Padding padding;

    /**
     * 密钥，支持三种密钥长度：128、192、256位
     */
    private String key;

    /**
     * 偏移向量，加盐
     */
    private String iv;

}

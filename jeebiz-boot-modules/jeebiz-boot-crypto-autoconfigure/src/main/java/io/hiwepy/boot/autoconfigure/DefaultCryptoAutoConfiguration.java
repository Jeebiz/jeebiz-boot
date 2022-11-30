package io.hiwepy.boot.autoconfigure;

import cn.hutool.crypto.symmetric.AES;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ObjectMapper.class})
@EnableConfigurationProperties(CryptoProperties.class)
public class DefaultCryptoAutoConfiguration {

    @Bean
    public AES aes(CryptoProperties cryptoProperties) {
        return new AES( cryptoProperties.getMode(), cryptoProperties.getPadding(),
                cryptoProperties.getKey().getBytes(StandardCharsets.UTF_8), cryptoProperties.getIv().getBytes(StandardCharsets.UTF_8));
    }

}

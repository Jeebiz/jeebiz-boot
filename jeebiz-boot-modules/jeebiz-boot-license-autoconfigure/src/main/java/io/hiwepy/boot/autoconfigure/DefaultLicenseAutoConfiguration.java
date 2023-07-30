package io.hiwepy.boot.autoconfigure;

import io.hiwepy.boot.autoconfigure.license.LicenseVerify;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({LicenseVerify.class})
@EnableConfigurationProperties({LicenseProperties.class})
public class DefaultLicenseAutoConfiguration {

    @Bean(initMethod = "installLicense", destroyMethod = "unInstallLicense")
    public LicenseVerify licenseVerify(LicenseProperties properties) {
        return new LicenseVerify(properties.getSubject(), properties.getPublicAlias(), properties.getStorePass(),
                properties.getLicensePath(), properties.getPublicKeysStorePath());
    }

}

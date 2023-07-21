package io.hiwepy.boot.demo.setup.ehcache;


import java.util.concurrent.TimeUnit;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.stereotype.Component;

/**
 * 代码方式创建缓存配置示例
 */
@Component
public class CachingSetup implements JCacheManagerCustomizer {

	@Override
	public void customize(CacheManager cacheManager) {
		cacheManager.createCache("people",
				new MutableConfiguration<>()
						.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 10)))
						.setStoreByValue(false)
						.setStatisticsEnabled(true));
	}

}

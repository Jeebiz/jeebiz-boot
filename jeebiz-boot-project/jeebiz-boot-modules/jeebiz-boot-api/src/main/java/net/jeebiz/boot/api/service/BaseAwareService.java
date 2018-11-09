package net.jeebiz.boot.api.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BaseAwareService implements InitializingBean, ApplicationContextAware{
	
	protected ApplicationContext context = null;
	/**核心缓存名称*/
	protected static final String DEFAULT_CACHE = "defaultCache";
	
	@Autowired(required = false)
	protected CacheManager cacheManager;
	protected String cacheName = DEFAULT_CACHE;
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	public ApplicationContext getContext() {
		return context;
	}
	
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String getCacheName() {
		return cacheName == null ? DEFAULT_CACHE : cacheName;
	}
	
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	public Cache getCache() {
		return getCacheManager().getCache(getCacheName());
	}
	
}

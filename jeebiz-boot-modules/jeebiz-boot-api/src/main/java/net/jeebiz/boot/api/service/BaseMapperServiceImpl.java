/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.context.NestedMessageSource;
import org.springframework.biz.web.servlet.support.RequestContextUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dozermapper.core.Mapper;

import net.jeebiz.boot.api.dao.BaseMapper;
import net.jeebiz.boot.api.dao.entities.PaginationEntity;
import net.jeebiz.boot.api.dao.entities.PairModel;

/**
 * 通用Service实现，daoBase自动注入，不能存在多个实例
 * 
 * @author <a href="https://github.com/wandl">wandl</a>
 * @param <T> {@link IBaseMapperService} 持有的实体对象
 * @param <E> {@link BaseMapper} 实现
 */
public class BaseMapperServiceImpl<T extends Model<?>, E extends BaseMapper<T>> extends ServiceImpl<E, T> implements InitializingBean,
		ApplicationEventPublisherAware, ApplicationContextAware, EmbeddedValueResolverAware, IBaseMapperService<T> {

	protected static Logger LOG = LoggerFactory.getLogger(BaseServiceImpl.class);

	/**核心缓存名称*/
	protected static final String DEFAULT_CACHE = "defaultCache";
	protected String cacheName = DEFAULT_CACHE;
	
	private StringValueResolver valueResolver;
	private ApplicationEventPublisher eventPublisher;
	private ApplicationContext context;
	
	@Autowired
	private NestedMessageSource messageSource;
	@Autowired(required = false)
	protected CacheManager cacheManager;
	@Autowired
	private Mapper beanMapper; 
	

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	/**
	 * 获取国际化信息
	 * @param key 国际化Key
	 * @param args 参数
	 * @return 国际化字符串
	 */
	protected String getMessage(String key, Object... args) {
		//两个方法在没有使用JSF的项目中是没有区别的
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		//				                      RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		//HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();
		return getMessageSource().getMessage(key, args, RequestContextUtils.getLocale(request));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setStatus(String id, String status) {
		return getBaseMapper().setStatus(id, status);
	}
	
	/**
	 * 查询单条数据
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public T getModel(String id) {
		return getBaseMapper().getModel(id);
	}

	/**
	 * 分页查询
	 * 
	 * @param t
	 * @return
	 */
	@Override
	public Page<T> getPagedList(PaginationEntity<T> model) {

		Page<T> page = new Page<T>(model.getPageNo(), model.getLimit());
		if(!CollectionUtils.isEmpty(model.getOrders())) {
			for (OrderItem orderBy : model.getOrders()) {
				page.addOrder(orderBy);
			}
		}
		List<T> records = getBaseMapper().getPagedList(page, model);
		page.setRecords(records);

		return page;
	}

	/**
	 * 分页查询
	 */
	@Override
	public Page<T> getPagedList(Page<T> page, PaginationEntity<T> model) {

		List<T> records = getBaseMapper().getPagedList(page, model);
		page.setRecords(records);

		return page;
	}

	/**
	 * 无分页查询
	 * 
	 * @param t
	 * @return
	 */
	@Override
	public List<T> getModelList(T t) {
		return getBaseMapper().getModelList(t);
	}

	/**
	 * 无分页查询
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public List<T> getModelList(String key) {
		return getBaseMapper().getModelList(key);
	}

	/**
	 * 统计记录数
	 * 
	 * @param t
	 * @return
	 */
	@Override
	public int getCount(T t) {
		return getBaseMapper().getCount(t);
	}

	@Override
	public int getCountByUid(String uid) {
		return getBaseMapper().getCountByUid(uid);
	}
	
	@Override
	public int getCountByCode(String code, String origin) {
		return getBaseMapper().getCountByCode(code, origin);
	}

	@Override
	public int getCountByName(String name, String origin) {
		return getBaseMapper().getCountByName(name, origin);
	}
	
	@Override
	public int getCountByParent(String parent) {
		return getBaseMapper().getCountByParent(parent);
	}
	
	
	@Override
	public String getValue(String key) {
		return getBaseMapper().getValue(key);
	}
	
	@Override
	public Map<String, String> getValues(String key) {
		return getBaseMapper().getValues(key);
	}

	@Override
	public List<PairModel> getPairValues(String key) {
		return getBaseMapper().getPairValues(key);
	}

	@Override
	public List<PairModel> getPairList() {
		return getBaseMapper().getPairList();
	}

	public StringValueResolver getValueResolver() {
		return valueResolver;
	}

	public void setValueResolver(StringValueResolver valueResolver) {
		this.valueResolver = valueResolver;
	}

	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public MessageSource getMessageSource() {
		return messageSource;
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
	
	public Mapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(Mapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.valueResolver = resolver;
	}

}

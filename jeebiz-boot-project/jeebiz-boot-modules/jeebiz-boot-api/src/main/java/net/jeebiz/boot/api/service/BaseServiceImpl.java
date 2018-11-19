package net.jeebiz.boot.api.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringValueResolver;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.api.dao.entities.PaginationModel;
import net.jeebiz.boot.api.dao.entities.PairModel;

/**
 * 通用Service实现，daoBase自动注入，不能存在多个实例
 * 
 * @author <a href="https://github.com/vindell">vindell</a>
 * @param <T> {@link BaseService} 持有的实体对象
 * @param <E> {@link BaseDao} 实现
 */
public class BaseServiceImpl<T, E extends BaseDao<T>> extends BaseAwareService
		implements ApplicationEventPublisherAware, ApplicationContextAware, MessageSourceAware,
		EmbeddedValueResolverAware, BaseService<T> {

	protected static Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
	private StringValueResolver valueResolver;
	private ApplicationEventPublisher eventPublisher;
	private ApplicationContext context;
	private MessageSource messageSource;
	
	@Autowired
	protected E dao;

	/**
	 * Dao实现注入
	 * 
	 * @param dao
	 */
	public void setDao(E dao) {
		this.dao = dao;
	}

	/**
	 * 增加记录
	 * @param t 实体对象
	 * @return 是否增加成功
	 */
	@Transactional
	public int insert(T t) {
		return dao.insert(t);
	}

	/**
	 * 修改记录
	 * @param t
	 * @return
	 */
	@Transactional
	public int update(T t) {
		return dao.update(t);
	}

	@Transactional
	public int delete(String id) {
		return dao.delete(id);
	}
	
	@Transactional
	public int delete(T t) {
		return dao.delete(t);
	}

	/**
	 * 查询单条数据
	 * @param id
	 * @return
	 */
	@Transactional
	public T getModel(String id) {
		return dao.getModel(id);
	}

	/**
	 * 查询单条数据
	 * 
	 * @param t
	 * @return
	 */
	@Transactional
	public T getModel(T t) {
		return dao.getModel(t);
	}

	/**
	 * 批量删除
	 * @param map
	 * @return
	 */
	@Transactional
	public int batchDelete(Map<String, Object> map) {
		return dao.batchDelete(map);
	}

	/**
	 * 批量删除
	 * @param list
	 * @return
	 */
	@Transactional
	public int batchDelete(List<?> list) {
		return dao.batchDelete(list);
	}

	/**
	 * 批量删除
	 * @param map
	 * @return
	 */
	@Transactional
	public int batchUpdate(Map<String, Object> map) {
		return dao.batchUpdate(map);
	}

	@Override
	@Transactional
	public int batchUpdate(List<T> list) {
		return dao.batchUpdate(list);
	}

	@Override
	@Transactional
	public int setStatus(String id, String status) {
		return dao.setStatus(id, status);
	}
	
	/**
	 * 分页查询
	 * 
	 * @param t
	 * @return
	 */
	@Override
	@Transactional
	public Page<T> getPagedList(T t) {
		
		PaginationModel tModel = (PaginationModel) t;
		
		Page<T> page = new Page<T>(tModel.getPageNo(), tModel.getLimit());
		if("asc".equalsIgnoreCase(tModel.getSortOrder())) {
			page.setAsc(tModel.getSortName());
		} else {
			page.setDesc(tModel.getSortName());
		}
		List<T> records = dao.getPagedList(page, t);
		page.setRecords(records);
		
		return page;
	}
	
	/**
	 * 分页查询
	 */
	@Override
	@Transactional
	public Page<T> getPagedList(Page<T> page,T t) {
		
		List<T> records = dao.getPagedList(page, t);
		page.setRecords(records);
		
		return page;
	}


	/**
	 * 无分页查询
	 * 
	 * @param t
	 * @return
	 */
	public List<T> getModelList(T t) {
		return dao.getModelList(t);
	}

	/**
	 * 无分页查询
	 * 
	 * @param key
	 * @return
	 */
	public List<T> getModelList(String key) {
		return dao.getModelList(key);
	}

	/**
	 * 统计记录数
	 * 
	 * @param t
	 * @return
	 */
	public int getCount(T t) {
		return dao.getCount(t);
	}

	public String getValue(String key) {
		return dao.getValue(key);
	}

	public Map<String, String> getValues(String key) {
		return dao.getValues(key);
	}
	
	@Override
	public List<PairModel> getPairValues(String key) {
		return dao.getPairValues(key);
	}

	public E getDao() {
		return dao;
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

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

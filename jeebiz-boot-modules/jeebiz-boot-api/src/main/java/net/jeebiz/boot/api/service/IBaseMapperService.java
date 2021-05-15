/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import net.jeebiz.boot.api.dao.entities.PaginationEntity;
import net.jeebiz.boot.api.dao.entities.PairModel;

/**
 * 通用Service接口
 * @author <a href="https://github.com/wandl">wandl</a>
 * @param <T> 持有的实体对象
 */
/**
 * 通用Service接口
 * 
 * @author <a href="https://github.com/wandl">wandl</a>
 * @param <T> 持有的实体对象
 */
public interface IBaseMapperService<T extends Model<?>> extends IService<T> {

	/**
	 * 更新数据状态
	 * 
	 * @param id
	 * @param status
	 * @return
	 */
	public int setStatus(String id, String status);

	/**
	 * 分页查询
	 * 
	 * @param t
	 * @return
	 */
	public Page<T> getPagedList(PaginationEntity<T> model);

	public Page<T> getPagedList(Page<T> page, PaginationEntity<T> model);

	/**
	 * 无分页查询
	 * 
	 * @param t
	 * @return
	 */
	public List<T> getEntityList(T t);

	/**
	 * 无分页查询<br>
	 * <p>
	 * MyBatis中对重载方法支持缺陷：XML中使用该方法映射无<br>
	 * 参和一个string参数会无法映射，建议XML中仅映射此方法一次，<br>
	 * 若有其它类似业务在自己接口中定义其它方法。<br>
	 * </p>
	 * 
	 * @param key
	 * @return
	 */
	public List<T> getEntityList(String key);

	/**
	 * 统计记录数
	 * @param t
	 * @return
	 */
	public int getCount(T t);
	
	/**
	 * 根据唯一ID编码获取记录数
	 * 
	 * @param name
	 * @return
	 */
	public int getCountByUid(String uid);

	/**
	 * 根据编码获取记录数
	 * 
	 * @param code
	 * @param origin
	 * @return
	 */
	public int getCountByCode(String code, String origin);

	/**
	 * 根据名称获取记录数
	 * 
	 * @param name
	 * @param origin
	 * @return
	 */
	public int getCountByName(String name, String origin);

	public int getCountByParent(String parent);
	
	/**
	 * 
	 * 通过指定key查询对应的唯一值
	 * @param key
	 * @return
	 */
	public String getValue(String key);
	
	/**
	 * 通过指定key查询多个值
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> getValues(String key);

	/**
	 * 根据key查询该分组下的基础数据
	 * 
	 * @param key
	 * @return
	 */
	public List<PairModel> getPairValues(String key);

	/**
	 * 查询一组键值对数据
	 * 
	 * @param key
	 * @return
	 */
	public List<PairModel> getPairList();

}

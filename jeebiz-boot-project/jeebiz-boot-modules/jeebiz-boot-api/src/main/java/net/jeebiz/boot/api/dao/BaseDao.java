/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.jeebiz.boot.api.dao.entities.PairModel;

/**
 * 通用Dao接口
 * @author <a href="https://github.com/vindell">wandl</a>
 * @param <T> 持有的实体对象
 */
public interface BaseDao <T> extends BaseMapper<T> {

	/**
	 * 增加记录
	 * @param t
	 * @return
	 */
	public int insert(T t);
	
	/**
	 * 修改记录
	 * @param t
	 * @return
	 */
	public int update(T t);
	
	/**
	 * 删除记录
	 * @param id
	 * @return
	 */
	public int delete(String id);
	/**
	 * 删除记录
	 * @param t
	 * @return
	 */
	public int delete(T t);
	
	
	/**
	 * 查询单条数据
	 * @param id
	 * @return
	 */
	public T getModel(String id);
	
	/**
	 * 查询单条数据
	 * @param t
	 * @return
	 */
	public T getModel(T t) ;
	
	/**
	 * 批量删除
	 * @param map
	 * @return
	 */
	public int batchDelete(Map<String,Object> map);
	
	
	
	/**
	 * 批量删除
	 * @param list
	 * @return
	 */
	public int batchDelete(List<?> list);
	
	/**
	 * 批量修改
	 * @param map
	 * @return
	 */
	public int batchUpdate(Map<String,Object> map);
	
	public int batchUpdate(List<T> list);
	
	/**
	 * 更新数据状态
	 * @param id
	 * @param status
	 * @return
	 */
	public int setStatus(@Param("id") String id, @Param("status") String status);
	
	/**
	 * 分页查询
	 * @param t
	 * @return
	 */
	public List<T> getPagedList(Page<T> page, @Param("model") T t);
	
	/**
	 * 无分页查询
	 * @param t
	 * @return
	 */
	public List<T> getModelList(T t);
	
	
	/**
	 * 无分页查询
	 * @param key
	 * @return
	 */
	public List<T> getModelList(String key);
	
	/**
	 * 统计记录数
	 * @param t
	 * @return
	 */
	public int getCount(T t);

	/**
	 * 根据唯一ID编码获取记录数
	 * @param name
	 * @return
	 */
	public int getCountByUid(@Param("uid") String uid);
	
	/**
	 * 根据编码获取记录数
	 * @param code
	 * @param origin
	 * @return
	 */
	public int getCountByCode(@Param("code") String code, @Param("origin") String origin);
	
	/**
	 * 根据名称获取记录数
	 * @param name
	 * @param origin
	 * @return
	 */
	public int getCountByName(@Param("name") String name, @Param("origin") String origin);
	
	public int getCountByParent(@Param("parent") String parent);
	
	/**
	 * 按数据范围分页查询
	 * @param t
	 * @return
	 */
	public List<T> getPagedByScope(T t);
	
	/**
	 * 按数据范围无分页查询
	 * @param t
	 * @return
	 */
	public List<T> getModelListByScope(T t);
	
	/**
	 * 通过指定key查询对应的唯一值
	 * @param key
	 * @return
	 */
	public String getValue(@Param("key") String key);
	
	/**
	 * 通过指定key查询多个值
	 * @param key
	 * @return
	 */
	public Map<String, String> getValues(@Param("key") String key);
	
	/**
	 * 根据key查询该分组下的基础数据
	 * @param key
	 * @return
	 */
	public List<PairModel> getPairValues(@Param("key") String key);
	
	/**
	 *  查询一组键值对数据
	 * @param key
	 * @return
	 */
	public List<PairModel> getPairList();
	
}

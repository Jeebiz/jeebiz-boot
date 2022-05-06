/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.hiwepy.boot.api.dao.entities.PaginationEntity;
import io.hiwepy.boot.api.dao.entities.PairModel;

/**
 * 通用Dao接口
 * @author <a href="https://github.com/wandl">wandl</a>
 * @param <T> 持有的实体对象
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T>  {

	/**
	 * 查询单条数据
	 * @param id
	 * @return
	 */
	public T getModel(String id);
	
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
	public List<T> getPagedList(Page<T> page, @Param("model") PaginationEntity<T> model);

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
	public List<T> getModelList(@Param("key") String key);

	/**
	 * 统计记录数
	 * @param t
	 * @return
	 */
	public Long getCount(T t);

	/**
	 * 根据唯一ID编码获取记录数
	 * @param uid
	 * @return
	 */
	public Long getCountByUid(@Param("uid") String uid);

	/**
	 * 根据编码获取记录数
	 * @param code
	 * @param origin
	 * @return
	 */
	public Long getCountByCode(@Param("code") String code, @Param("origin") String origin);

	/**
	 * 根据名称获取记录数
	 * @param name
	 * @param origin
	 * @return
	 */
	public Long getCountByName(@Param("name") String name, @Param("origin") String origin);

	public Long getCountByParent(@Param("parent") String parent);

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

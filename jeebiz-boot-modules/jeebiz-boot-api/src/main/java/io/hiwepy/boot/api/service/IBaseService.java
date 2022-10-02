/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.hiwepy.boot.api.dao.entities.PaginationEntity;
import io.hiwepy.boot.api.dao.entities.PairModel;

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
public interface IBaseService<T> extends IService<T> {

	/**
	 * 更新数据状态
	 *
	 * @param id
	 * @param status
	 * @return
	 */
	boolean setStatus(Serializable id, String status);

	/**
	 * 分页查询
	 *
	 * @param model
	 * @return
	 */
	Page<T> getPagedList(PaginationEntity<T> model);

	Page<T> getPagedList(Page<T> page, PaginationEntity<T> model);

	/**
	 * 无分页查询
	 *
	 * @param t
	 * @return
	 */
	List<T> getEntityList(T t);

	/**
	 * 统计记录数
	 * @param t
	 * @return
	 */
	Long getCount(T t);

	/**
	 * 根据唯一ID编码获取记录数
	 *
	 * @param uid
	 * @return
	 */
	Long getCountByUid(Serializable uid);

	/**
	 * 根据编码获取记录数
	 *
	 * @param code
	 * @param origin
	 * @return
	 */
	Long getCountByCode(String code, String origin);

	/**
	 * 根据名称获取记录数
	 *
	 * @param name
	 * @param origin
	 * @return
	 */
	Long getCountByName(String name, String origin);

	Long getCountByParent(String parent);

	/**
	 *
	 * 通过指定key查询对应的唯一值
	 * @param key
	 * @return
	 */
	String getValue(String key);

	/**
	 * 通过指定key查询多个值
	 *
	 * @param key
	 * @return
	 */
	Map<String, String> getValues(String key);

	/**
	 * 根据key查询该分组下的基础数据
	 *
	 * @param key
	 * @return
	 */
	List<PairModel> getPairValues(String key);


	Map<String, List<PairModel>> getPairValues(String[] keyArr);

	/**
	 * 查询一组键值对数据
	 *
	 * @param key
	 * @return
	 */
	List<PairModel> getPairList();

}

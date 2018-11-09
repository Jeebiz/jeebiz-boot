/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

import net.jeebiz.boot.api.utils.StringUtils;

public class AuthzPermsUtils {
	
	/**
	 * 对提交的权限标记进行去重处理
	 * @param originPerms ： 原始权限标记集合
	 * @return 解析和去重后的标记集合
	 */
	public static List<String> distinct(Set<String> originPerms){
		List<String> perms = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(originPerms)) {
			return distinct(originPerms.stream().collect(Collectors.toList()));
		}
		return perms;
	}
	
	/**
	 * 对提交的权限标记进行去重处理
	 * @param originPerms ： 原始权限标记集合
	 * @return 解析和去重后的标记集合
	 */
	public static List<String> distinct(List<String> originPerms){
		List<String> perms = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(originPerms)) {
			// 权限标记处理，这里的每个元素可能是多个标记的组合
			for(String perm : originPerms) {
				perm = StringUtils.trimAllWhitespace(perm);
				if(StringUtils.hasText(perm)) {
					Collections.addAll(perms, StringUtils.tokenizeToStringArray(perm));
				}
			}
			// 组合最终的标记
			return perms.stream().distinct().sorted().collect(Collectors.toList());
		}
		return perms;
	}
	
	/**
	 * 获取授权标记增量集合
	 * @param perms		：此次提交的授权标记
	 * @param oldperms	： 已经授权标记
	 * @return
	 */
	public static List<String> increment(List<String> perms, List<String> oldperms){
		if(CollectionUtils.isEmpty(perms)) {
			return Lists.newArrayList(oldperms);
		}
		return perms.stream()
				.filter(perm -> !oldperms.contains(perm))
                .collect(Collectors.toList());
	}
	
	/**
	 * 获取授权标记减量集合
	 * @param perms		：此次提交的授权标记
	 * @param oldperms	： 已经授权标记
	 * @return
	 */
	public static List<String> decrement(List<String> perms, List<String> oldperms){
		if(CollectionUtils.isEmpty(oldperms)) {
			return Lists.newArrayList(perms);
		}
		return oldperms.stream()
				.filter(perm -> !perms.contains(perm))
                .collect(Collectors.toList());
	}
	
}

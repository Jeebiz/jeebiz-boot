package net.jeebiz.boot.authz.feature.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;

public final class FeatureNavUtils {
	
	protected static JSONArray getFeatureOptList(AuthzFeatureModel feature, List<AuthzFeatureOptModel> featureOptList) {
		JSONArray jsonArray = new JSONArray();
		// 筛选当前菜单对应的操作按钮
		List<AuthzFeatureOptModel> optList = featureOptList.stream()
				.filter(featureOpt -> StringUtils.equals(feature.getId(), featureOpt.getFeatureId()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(optList)){
			for (AuthzFeatureOptModel opt : optList) {
				JSONObject jsonObject  = new JSONObject();
				// 功能菜单ID
				jsonObject.put("id", feature.getId() + "_" + opt.getId());
				// 功能操作名称
				jsonObject.put("name", opt.getName());
				jsonObject.put("label", opt.getName());
				// 功能操作图标样式
				jsonObject.put("icon", opt.getIcon());
				// 功能操作排序
				jsonObject.put("order", opt.getOrder());
				// 功能菜单ID
				jsonObject.put("parent", opt.getFeatureId());
				// 功能操作是否可见(1:可见|0:不可见)
				jsonObject.put("visible", opt.getVisible());
				// 功能操作权限标记
				jsonObject.put("perms", opt.getPerms());
				
				jsonArray.add(jsonObject);
			}
			return jsonArray;
		}
		return null;
	}
	
	protected static JSONArray getSubFeatureList(AuthzFeatureModel parentNav,List<AuthzFeatureModel> featureList, List<AuthzFeatureOptModel> featureOptList) {
		
		JSONArray jsonArray = new JSONArray();
		//筛选当前父功能模块节点的子功能模块节点数据
		List<AuthzFeatureModel> childFeatureList = featureList.stream()
				.filter(feature -> StringUtils.equals(parentNav.getId(), feature.getParent()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(childFeatureList)){
			for (AuthzFeatureModel feature : childFeatureList) {
				JSONObject jsonObject  = new JSONObject();
				// 功能菜单ID
				jsonObject.put("id", feature.getId());
				// 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
				jsonObject.put("code", feature.getCode());
				// 功能菜单名称
				jsonObject.put("name", feature.getName());
				// 功能菜单简称
				jsonObject.put("label", feature.getAbb());
				jsonObject.put("abb", feature.getAbb());
				// 菜单类型(1:原生|2:自定义)
				jsonObject.put("type", feature.getType());
				// 菜单样式或菜单图标路径
				jsonObject.put("icon", feature.getIcon());
				// 菜单显示顺序
				jsonObject.put("order", feature.getOrder());
				// 父级功能菜单ID
				jsonObject.put("parent", feature.getParent());
				// 菜单是否可见(1:可见|0:不可见)
				jsonObject.put("visible", feature.getVisible());
				// 菜单所拥有的权限标记
				jsonObject.put("perms", feature.getPerms());
				// 路径为#表示有子菜单
				if(StringUtils.equals("#", feature.getUrl())){
					jsonObject.put("children", getSubFeatureList(feature, featureList, featureOptList));
				} else {
					// 当前菜单的操作按钮
					JSONArray featureOpts  = getFeatureOptList(feature, featureOptList);
					if(null != featureOpts && featureOpts.size() > 0) {
						jsonObject.put("children", featureOpts);
					}
				}
				jsonArray.add(jsonObject);
			}
			return jsonArray;
		}
		return jsonArray;
	}
	
	public static JSONArray getFeatureTreeList(List<AuthzFeatureModel> featureList, List<AuthzFeatureOptModel> featureOptList) {
		
		JSONArray jsonArray = new JSONArray();
		//优先获得最顶层的菜单集合
		List<AuthzFeatureModel> topFeatureList = featureList.stream()
				.filter(feature ->  StringUtils.equals("0", feature.getId()))
				.collect(Collectors.toList());
		for (AuthzFeatureModel feature : topFeatureList) {
			JSONObject jsonObject  = new JSONObject();
			// 功能菜单ID
			jsonObject.put("id", feature.getId());
			// 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
			jsonObject.put("code", feature.getCode());
			// 功能菜单名称
			jsonObject.put("name", feature.getName());
			// 功能菜单简称
			jsonObject.put("label", feature.getAbb());
			jsonObject.put("abb", feature.getAbb());
			// 菜单类型(1:原生|2:自定义)
			jsonObject.put("type", feature.getType());
			// 菜单样式或菜单图标路径
			jsonObject.put("icon", feature.getIcon());
			// 菜单显示顺序
			jsonObject.put("order", feature.getOrder());
			// 父级功能菜单ID
			jsonObject.put("parent", feature.getParent());
			// 菜单是否可见(1:可见|0:不可见)
			jsonObject.put("visible", feature.getVisible());
			// 菜单所拥有的权限标记
			jsonObject.put("perms", feature.getPerms());
			// 子菜单
			JSONArray subFeatures  = getSubFeatureList(feature, featureList, featureOptList);
			if(null != subFeatures && subFeatures.size() > 0) {
				jsonObject.put("children", subFeatures);
			}
			
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	public static JSONArray getFeatureFlatList(List<AuthzFeatureModel> featureList, List<AuthzFeatureOptModel> featureOptList) {
		JSONArray jsonArray = new JSONArray();
		// 筛选菜单中的末节点的功能菜单
		List<AuthzFeatureModel> leafFeatureList = featureList.stream()
				.filter(feature -> StringUtils.isNotEmpty(feature.getParent())
						&& !StringUtils.equals("0", feature.getParent()) && !StringUtils.equals("#", feature.getUrl()))
				.collect(Collectors.toList());
		for (AuthzFeatureModel feature : leafFeatureList) {
			JSONObject jsonObject  = new JSONObject();
			// 功能菜单ID
			jsonObject.put("id", feature.getId());
			// 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
			jsonObject.put("code", feature.getCode());
			// 功能菜单名称
			jsonObject.put("name", feature.getName());
			// 功能菜单简称
			jsonObject.put("abb", feature.getAbb());
			// 菜单类型(1:原生|2:自定义)
			jsonObject.put("type", feature.getType());
			// 菜单样式或菜单图标路径
			jsonObject.put("icon", feature.getIcon());
			// 菜单显示顺序
			jsonObject.put("order", feature.getOrder());
			// 父级功能菜单ID
			jsonObject.put("parent", feature.getParent());
			// 菜单是否可见(1:可见|0:不可见)
			jsonObject.put("visible", feature.getVisible());
			// 菜单所拥有的权限标记
			jsonObject.put("perms", feature.getPerms());
			// Url属性不为#表示有父级菜单
			if(!StringUtils.equals("#", feature.getUrl())){
				jsonObject.put("label", getLabel(new StringBuilder(feature.getName()) , feature, featureList));
			} else {
				jsonObject.put("label", feature.getName());
			}
			
			JSONArray featureOpts  = getFeatureOptList(feature, featureOptList);
			if(null != featureOpts && featureOpts.size() > 0) {
				// 当前菜单的操作按钮
				jsonObject.put("children", featureOpts);
			}
			
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}
	
	protected static StringBuilder getLabel(StringBuilder builder,AuthzFeatureModel leaf,List<AuthzFeatureModel> featureList) {
		// 获取该菜单的父菜单
		List<AuthzFeatureModel> parentFeatureList = featureList.stream()
				.filter(feature ->  StringUtils.equals(leaf.getParent(), feature.getId()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(parentFeatureList)) {
			// 只有一个父亲，只会循环一次
			for (AuthzFeatureModel feature : parentFeatureList) {
				builder.insert(0, "/").insert(0, feature.getName());
				// Url属性不为#表示有父级菜单
				if(!StringUtils.equals("#", feature.getUrl())){
					getLabel(builder , feature, featureList);
				}
			}
		}
		return builder;
	}
	
	/**
	 * @param features		： 所有的功能菜单
	 * @param ownFeatures	： 用户/角色拥有的菜单
	 * @return
	 */
	public static List<AuthzFeatureModel> getFeatureMergedList(List<AuthzFeatureModel> features,
			List<AuthzFeatureModel> ownFeatures) {
		// 定义合并后的功能菜单集合对象
		List<AuthzFeatureModel> mergedFeatureList = Lists.newArrayList();
		// 循环用户拥有的菜单
		for (AuthzFeatureModel feature : ownFeatures) {
			mergedFeatureList.add(feature);
			// 从系统菜单中获取当前菜单的所有的父级菜单，并加入合并集合
			getParants(mergedFeatureList, feature, features );
		}
		// 去除重复的菜单
		List<AuthzFeatureModel> mergedFeatures = Lists.newArrayList();
		for (AuthzFeatureModel feature : mergedFeatureList) {
			 boolean b = mergedFeatures.stream().anyMatch(u -> u.getId().equals(feature.getId()));
		     if (!b) {
		    	 mergedFeatures.add(feature);
		     }
		}
		return mergedFeatures;
	}
	
	/**
	 * 
	 * @param mergedFeatures 	： 合并后的功能菜单集合对象
	 * @param currentFeature	： 用户拥有的菜单
	 * @param featureList		： 所有的功能菜单
	 * @return
	 */
	protected static List<AuthzFeatureModel> getParants(List<AuthzFeatureModel> mergedFeatures, AuthzFeatureModel currentFeature, List<AuthzFeatureModel> featureList) {
		// 获取该菜单的父菜单
		List<AuthzFeatureModel> parentFeatureList = featureList.stream()
				.filter(feature ->  StringUtils.equals(currentFeature.getParent(), feature.getId()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(parentFeatureList)) {
			// 只有一个父亲，只会循环一次
			for (AuthzFeatureModel prentFeature : parentFeatureList) {
				mergedFeatures.add(prentFeature);
				getParants(mergedFeatures, prentFeature, featureList );
			}
		}
		return parentFeatureList;
	}
	
}

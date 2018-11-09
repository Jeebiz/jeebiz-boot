package net.jeebiz.boot.authz.feature.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;

public final class FeatureNavUtils {
	
	public static <T> List<T> findAll(Collection<T> inputCollection, Predicate predicate) {
        if (inputCollection != null && predicate != null) {
        	List<T> outputCollection = new ArrayList<T>();
        	CollectionUtils.select(inputCollection, predicate, outputCollection);
            return outputCollection;
        }
        return null;
    }
	
	protected static JSONArray getFeatureOptList(AuthzFeatureModel feature, List<AuthzFeatureOptModel> featureOptList) {
		JSONArray jsonArray = new JSONArray();
		// 筛选当前菜单对应的操作按钮
		List<AuthzFeatureOptModel> optList = FeatureNavUtils.findAll(featureOptList, new FeatureOptPredicate(feature.getId()));
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
		Predicate navPredicate = FeaturePredicateUtils.childrenPredicate(parentNav.getId());
		//筛选当前父功能模块节点的子功能模块节点数据
		List<AuthzFeatureModel> childFeatureList = FeatureNavUtils.findAll(featureList, navPredicate);
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
		Predicate navPredicate = FeaturePredicateUtils.childrenPredicate("0");
		List<AuthzFeatureModel> topFeatureList = FeatureNavUtils.findAll(featureList, navPredicate);
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
		List<AuthzFeatureModel> leafFeatureList = FeatureNavUtils.findAll(featureList, FeaturePredicateUtils.leafPredicate());
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
			// Parent属性不为0表示不是一级菜单
			if(!StringUtils.equals("0", feature.getParent())){
				jsonObject.put("label", getLabel(new StringBuilder(feature.getAbb()) , feature, featureList));
			} else {
				jsonObject.put("label", feature.getAbb());
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
		List<AuthzFeatureModel> parentFeatureList = FeatureNavUtils.findAll(featureList, FeaturePredicateUtils.parentPredicate(leaf.getParent()));
		if(CollectionUtils.isNotEmpty(parentFeatureList)) {
			// 只有一个父亲，只会循环一次
			for (AuthzFeatureModel feature : parentFeatureList) {
				builder.insert(0, "/").insert(0, feature.getAbb());
				if(!StringUtils.equals("0", feature.getParent())){
					getLabel(builder , feature, featureList);
				}
			}
		}
		return builder;
	}
	
	public static List<AuthzFeatureModel> getFeatureMergedList(List<AuthzFeatureModel> features,
			List<AuthzFeatureModel> userFeatures) {
		List<AuthzFeatureModel> mergedFeatureList = Lists.newArrayList();
		for (AuthzFeatureModel feature : userFeatures) {
			mergedFeatureList.add(feature);
			getParants(mergedFeatureList, feature, features );
		}
		List<AuthzFeatureModel> mergedFeatures = Lists.newArrayList();
		for (AuthzFeatureModel feature : mergedFeatureList) {
			 boolean b = mergedFeatures.stream().anyMatch(u -> u.getId().equals(feature.getId()));
		     if (!b) {
		    	 mergedFeatures.add(feature);
		     }
		}
		return mergedFeatures;
	}
	
	protected static List<AuthzFeatureModel> getParants(List<AuthzFeatureModel> mergedFeatures, AuthzFeatureModel feature, List<AuthzFeatureModel> featureList) {
		List<AuthzFeatureModel> parentFeatureList = FeatureNavUtils.findAll(featureList, FeaturePredicateUtils.parentPredicate(feature.getParent()));
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

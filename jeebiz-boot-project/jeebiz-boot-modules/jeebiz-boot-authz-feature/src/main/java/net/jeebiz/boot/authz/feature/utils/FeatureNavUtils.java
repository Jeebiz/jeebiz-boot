package net.jeebiz.boot.authz.feature.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;
import net.jeebiz.boot.authz.feature.web.vo.AuthzFeatureOptVo;
import net.jeebiz.boot.authz.feature.web.vo.AuthzFeatureVo;

public final class FeatureNavUtils {
	
	protected static List<AuthzFeatureOptVo> getFeatureOptList(AuthzFeatureModel feature, List<AuthzFeatureOptModel> featureOptList) {
		List<AuthzFeatureOptVo> featureOpts = Lists.newArrayList();
		// 筛选当前菜单对应的操作按钮
		List<AuthzFeatureOptModel> optList = featureOptList.stream()
				.filter(featureOpt -> StringUtils.equals(feature.getId(), featureOpt.getFeatureId()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(optList)){
			for (AuthzFeatureOptModel opt : optList) {
				AuthzFeatureOptVo optVo  = new AuthzFeatureOptVo();
				// 功能菜单ID
				optVo.setId(feature.getId() + "_" + opt.getId());
				// 功能操作名称
				optVo.setName(opt.getName());
				// 功能操作图标样式
				optVo.setIcon(opt.getIcon());
				// 功能操作排序
				optVo.setOrder(opt.getOrder());
				// 功能菜单ID
				optVo.setFeatureId( opt.getFeatureId());
				// 功能操作是否可见(1:可见|0:不可见)
				optVo.setVisible(opt.getVisible());
				// 功能操作权限标记
				optVo.setPerms(opt.getPerms());
				
				featureOpts.add(optVo);
			}
			return featureOpts.stream().sorted().collect(Collectors.toList());
		}
		return featureOpts;
	}
	
	protected static List<AuthzFeatureVo> getSubFeatureList(AuthzFeatureModel parentNav,List<AuthzFeatureModel> featureList, List<AuthzFeatureOptModel> featureOptList) {
		
		List<AuthzFeatureVo> features = Lists.newArrayList();
		//筛选当前父功能模块节点的子功能模块节点数据
		List<AuthzFeatureModel> childFeatureList = featureList.stream()
				.filter(feature -> StringUtils.equals(parentNav.getId(), feature.getParent()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(childFeatureList)){
			for (AuthzFeatureModel feature : childFeatureList) {
				AuthzFeatureVo featureVo = new AuthzFeatureVo();
				// 功能菜单ID
				featureVo.setId(feature.getId());
				// 功能菜单简称
				featureVo.setAbb(feature.getAbb());
				// 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
				featureVo.setCode(feature.getCode());
				// 功能菜单名称
				featureVo.setName(feature.getName());
				// 菜单类型(1:原生|2:自定义)
				featureVo.setType(feature.getType());
				// 菜单样式或菜单图标路径
				featureVo.setIcon(feature.getIcon());
				// 菜单显示顺序
				featureVo.setOrder(feature.getOrder());
				// 父级功能菜单ID
				featureVo.setParent(feature.getParent());
				// 功能菜单URL
				featureVo.setUrl(feature.getUrl());
				// 菜单是否可见(1:可见|0:不可见)
				featureVo.setVisible(feature.getVisible());
				// 菜单所拥有的权限标记
				featureVo.setPerms(feature.getPerms());
				// 路径为#表示有子菜单
				if(StringUtils.equals("#", feature.getUrl())){
					// 子菜单
					List<AuthzFeatureVo> subFeatures = getSubFeatureList(feature, featureList, featureOptList);
					if(null != subFeatures && subFeatures.size() > 0) {
						boolean checked = subFeatures.stream().anyMatch(item -> item.isChecked());
						if(checked) {
							featureVo.setChecked(true);
						} else {
							featureVo.setChecked(false);
						}
						featureVo.setChildren(subFeatures);
					}
				} else {
					// 当前菜单的操作按钮
					List<AuthzFeatureOptVo> featureOpts  = getFeatureOptList(feature, featureOptList);
					if(null != featureOpts && featureOpts.size() > 0) {
						boolean checked = featureOpts.stream().anyMatch(item -> item.isChecked());
						if(checked) {
							featureVo.setChecked(true);
						} else {
							featureVo.setChecked(false);
						}
						featureVo.setOpts( featureOpts);
					}
				}
				features.add(featureVo);
			}
			return features.stream().sorted().collect(Collectors.toList());
		}
		return features;
	}
	
	public static List<AuthzFeatureVo> getFeatureTreeList(List<AuthzFeatureModel> featureList, List<AuthzFeatureOptModel> featureOptList) {
		
		//优先获得最顶层的菜单集合
		List<AuthzFeatureModel> topFeatureList = featureList.stream()
				.filter(feature -> StringUtils.equals("0", feature.getParent()))
				.collect(Collectors.toList());
		List<AuthzFeatureVo> features = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(topFeatureList)){
			
			for (AuthzFeatureModel feature : topFeatureList) {
				
				AuthzFeatureVo featureVo = new AuthzFeatureVo();
				// 功能菜单ID
				featureVo.setId(feature.getId());
				// 功能菜单简称
				featureVo.setAbb(feature.getAbb());
				// 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
				featureVo.setCode(feature.getCode());
				// 功能菜单名称
				featureVo.setName(feature.getName());
				// 菜单类型(1:原生|2:自定义)
				featureVo.setType(feature.getType());
				// 菜单样式或菜单图标路径
				featureVo.setIcon(feature.getIcon());
				// 菜单显示顺序
				featureVo.setOrder(feature.getOrder());
				// 父级功能菜单ID
				featureVo.setParent(feature.getParent());
				// 功能菜单URL
				featureVo.setUrl(feature.getUrl());
				// 菜单是否可见(1:可见|0:不可见)
				featureVo.setVisible(feature.getVisible());
				// 菜单所拥有的权限标记
				featureVo.setPerms(feature.getPerms());
				// 子菜单
				List<AuthzFeatureVo> subFeatures  = getSubFeatureList(feature, featureList, featureOptList);
				if(null != subFeatures && subFeatures.size() > 0) {
					boolean checked = subFeatures.stream().anyMatch(item -> item.isChecked());
					if(checked) {
						featureVo.setChecked(true);
					} else {
						featureVo.setChecked(false);
					}
					featureVo.setChildren(subFeatures);
				}
				features.add(featureVo);
			}
			return features.stream().sorted().collect(Collectors.toList());
		}
		return features;
	}
	
	public static List<AuthzFeatureVo> getFeatureFlatList(List<AuthzFeatureModel> featureList, List<AuthzFeatureOptModel> featureOptList) {
		List<AuthzFeatureVo> features = Lists.newArrayList();
		// 筛选菜单中的末节点的功能菜单
		List<AuthzFeatureModel> leafFeatureList = featureList.stream()
				.filter(feature -> StringUtils.isNotEmpty(feature.getParent())
						&& !StringUtils.equals("0", feature.getParent()) && !StringUtils.equals("#", feature.getUrl()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(leafFeatureList)){
			
			for (AuthzFeatureModel feature : leafFeatureList) {
				
				AuthzFeatureVo featureVo = new AuthzFeatureVo();
				// 功能菜单ID
				featureVo.setId(feature.getId());
				// 功能菜单简称
				featureVo.setAbb(feature.getAbb());
				// 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
				featureVo.setCode(feature.getCode());
				// 功能菜单名称
				featureVo.setName(feature.getName());
				// 菜单类型(1:原生|2:自定义)
				featureVo.setType(feature.getType());
				// 菜单样式或菜单图标路径
				featureVo.setIcon(feature.getIcon());
				// 菜单显示顺序
				featureVo.setOrder(feature.getOrder());
				// 父级功能菜单ID
				featureVo.setParent(feature.getParent());
				// 功能菜单URL
				featureVo.setUrl(feature.getUrl());
				// 菜单是否可见(1:可见|0:不可见)
				featureVo.setVisible(feature.getVisible());
				// 菜单所拥有的权限标记
				featureVo.setPerms(feature.getPerms());
				// Url属性不为#表示有父级菜单
				if(!StringUtils.equals("#", feature.getUrl())){
					featureVo.setLabel(getLabel(new StringBuilder(feature.getName()) , feature, featureList).toString());
				} else {
					featureVo.setLabel( feature.getName());
				}
				
				// 当前菜单的操作按钮
				List<AuthzFeatureOptVo> featureOpts  = getFeatureOptList(feature, featureOptList);
				if(null != featureOpts && featureOpts.size() > 0) {
					boolean checked = featureOpts.stream().anyMatch(item -> item.isChecked());
					if(checked) {
						featureVo.setChecked(true);
					} else {
						featureVo.setChecked(false);
					}
					featureVo.setOpts(featureOpts);
				}
				
				features.add(featureVo);
			}
			return features.stream().sorted().collect(Collectors.toList());
		}
		return features;
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

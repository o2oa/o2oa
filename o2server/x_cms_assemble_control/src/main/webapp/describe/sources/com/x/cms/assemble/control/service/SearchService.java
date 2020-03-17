package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.search.AppFilter;
import com.x.cms.assemble.control.jaxrs.search.CategoryFilter;
import com.x.cms.assemble.control.jaxrs.search.TopUnitNameFilter;
import com.x.cms.assemble.control.jaxrs.search.UnitNameFilter;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public class SearchService {

	// @MethodDescribe("根据用户列出所有可见的已发布文档涉及的应用栏目以及文档数量")
	public List<AppFilter> listAppInfoSearchFilter(EntityManagerContainer emc, List<String> app_ids, String docStatus,
			String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<AppFilter> appFilterList = new ArrayList<AppFilter>();
		AppFilter appFilter = null;
		List<String> appids_user = null;
		AppInfo appInfo = null;
		Business business = new Business(emc);

		// 先distinct用户可以访问的文档涉及到多少个可以访问的应用栏目
		appids_user = business.getSearchFactory().listDistinctAppInfoFromDocument(app_ids, docStatus, categoryId);
		if (appids_user != null && appids_user.size() > 0) {
			for (String appid : appids_user) {
				appInfo = emc.find(appid, AppInfo.class);
				if (appInfo != null) {
					appFilter = new AppFilter(appInfo.getId(), appInfo.getAppName(),
							business.getSearchFactory().getAppInfoDocumentCount(appid, docStatus, categoryId));
					appFilterList.add(appFilter);
				}
			}
		}
		return appFilterList;
	}

	// @MethodDescribe("根据用户列出所有可见的已发布文档涉及的应用分类以及文档数量")
	public List<CategoryFilter> listCategorySearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<CategoryFilter> categoryFilterList = new ArrayList<CategoryFilter>();
		CategoryFilter categoryFilter = null;
		List<String> categoryids_user = null;
		CategoryInfo categoryInfo = null;
		AppInfo appInfo = null;
		Business business = new Business(emc);
		// 先distinct用户可以访问的文档涉及到多少个可以访问的分类
		categoryids_user = business.getSearchFactory().listDistinctCategoryFromDocument(app_ids, docStatus, categoryId);
		if (categoryids_user != null && categoryids_user.size() > 0) {
			for (String _categoryId : categoryids_user) {
				categoryInfo = emc.find(_categoryId, CategoryInfo.class);
				if (categoryInfo != null) {
					appInfo = emc.find(categoryInfo.getAppId(), AppInfo.class);
					if (appInfo != null) {
						categoryFilter = new CategoryFilter(appInfo.getId(), appInfo.getAppName(), categoryInfo.getId(),
								categoryInfo.getCategoryName(), business.getSearchFactory()
										.getCategoryInfoDocumentCount(app_ids, _categoryId, docStatus));
						categoryFilterList.add(categoryFilter);
					}
				}
			}
		}
		return categoryFilterList;
	}

	// @MethodDescribe("根据用户列出所有可见的已发布文档涉及的拟稿顶层组织以及文档数量")
	public List<TopUnitNameFilter> listTopUnitNameSearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<TopUnitNameFilter> topUnitFilterList = new ArrayList<TopUnitNameFilter>();
		TopUnitNameFilter topUnitFilter = null;
		List<String> topUnitNames = null;
		Business business = new Business(emc);
		// 先distinct用户可以访问的文档涉及到多少个可以访问的分类
		topUnitNames = business.getSearchFactory().listDistinctTopUnitNameFromDocument(app_ids, docStatus, categoryId);
		if (topUnitNames != null && topUnitNames.size() > 0) {
			for (String creatorTopUnitName : topUnitNames) {
				// 循环进行数据统计
				topUnitFilter = new TopUnitNameFilter(creatorTopUnitName, creatorTopUnitName,
						business.getSearchFactory().getTopUnitDocumentCount(app_ids, creatorTopUnitName, docStatus,
								categoryId));
				topUnitFilterList.add(topUnitFilter);
			}
		}
		return topUnitFilterList;
	}

	// @MethodDescribe("根据用户列出所有可见的已发布文档涉及的拟稿组织以及文档数量")
	public List<UnitNameFilter> listUnitNameSearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<UnitNameFilter> unitFilterList = new ArrayList<UnitNameFilter>();
		UnitNameFilter unitNameFilter = null;
		List<String> unitNames = null;
		Business business = new Business(emc);
		// 先distinct用户可以访问的文档涉及到多少个可以访问的分类
		unitNames = business.getSearchFactory().listDistinctUnitNameFromDocument(app_ids, docStatus, categoryId);
		if (unitNames != null && unitNames.size() > 0) {
			for (String creatorUnitName : unitNames) {
				// 循环进行数据统计
				unitNameFilter = new UnitNameFilter(creatorUnitName, creatorUnitName, business.getSearchFactory()
						.getUnitDocumentCount(app_ids, creatorUnitName, docStatus, categoryId));
				unitFilterList.add(unitNameFilter);
			}
		}
		return unitFilterList;
	}

}

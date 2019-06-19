package com.x.cms.assemble.control.jaxrs.search;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.SearchServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;

class ActionListAppSearchFilterForDocStatus extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(SearchFilterAction.class);
	private AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	private SearchServiceAdv searchServiceAdv = new SearchServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	
	public ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson currentPerson, String docStatus, String categoryId) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		List<String> app_ids = null;
		List<AppFilter> appFilterList = null;
		List<CategoryFilter> categoryFilterList = null;
		List<TopUnitNameFilter> topUnitFilterList = null;
		List<UnitNameFilter> unitFilterList = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		Boolean isAnonymous = currentPerson.isAnonymous();
		String personName = currentPerson.getDistinguishedName();
		// 文档类型：全部|信息|数据
		String documentType = "信息";
		if (check) {
			try {
				isXAdmin = userManagerService.isManager(request, currentPerson);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e, 
						"系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (isXAdmin) {
				try {
					app_ids = appInfoServiceAdv.listAllIds(documentType);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSearchProcess(e, "系统在查询所有栏目信息ID列表时发生异常。");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			} else {
				try {
					List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
					List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
					app_ids = permissionQueryService.listViewableAppIdByPerson(
							personName, isAnonymous, unitNames, groupNames, null, null, documentType, 1000 );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSearchProcess( e, 
							"系统在根据用户权限查询所有可见的栏目信息时发生异常。Name:" + personName );
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		// 1、获取栏目统计列表
		if (check) {
			// 查询用户可访问的文档涉及到的所有栏目信息以及各栏目内文档数量
			try {
				appFilterList = searchServiceAdv.listAppInfoSearchFilter(app_ids, docStatus, categoryId );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e, 
						"系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有栏目名称列表时发生异常。"
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		// 2、获取分类统计列表
		if (check) {
			try {
				categoryFilterList = searchServiceAdv.listCategorySearchFilter(app_ids, docStatus, categoryId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e, 
						"系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有分类名称列表时发生异常。"
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				topUnitFilterList = searchServiceAdv.listTopUnitSearchFilter(app_ids, docStatus, categoryId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e, 
						"系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有顶层组织名称列表时发生异常。"
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				unitFilterList = searchServiceAdv.listUnitNameSearchFilter(app_ids, docStatus, categoryId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e, 
						"系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有组织名称列表时发生异常。"
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			wrap.setAppfileter_list(appFilterList);
			wrap.setCategoryfileter_list(categoryFilterList);
			wrap.setTopUnitNamefileter_list(topUnitFilterList);
			wrap.setUnitNamefileter_list(unitFilterList);
			result.setData(wrap);
		}
		return result;
	}
	
	public static class Wo {
		
		public static List<String> Excludes = new ArrayList<String>();

		private List<AppFilter> appfileter_list = null;
		
		private List<CategoryFilter> categoryfileter_list = null;
		
		private List<TopUnitNameFilter> topUnitfileter_list = null;
		
		private List<UnitNameFilter> unitfileter_list = null;
		
		public List<UnitNameFilter> getUnitNamefileter_list() {
			return unitfileter_list;
		}

		public void setUnitNamefileter_list(List<UnitNameFilter> unitfileter_list) {
			this.unitfileter_list = unitfileter_list;
		}

		public List<TopUnitNameFilter> getTopUnitNamefileter_list() {
			return topUnitfileter_list;
		}

		public void setTopUnitNamefileter_list(List<TopUnitNameFilter> topUnitfileter_list) {
			this.topUnitfileter_list = topUnitfileter_list;
		}

		public List<AppFilter> getAppfileter_list() {
			return appfileter_list;
		}

		public void setAppfileter_list(List<AppFilter> appfileter_list) {
			this.appfileter_list = appfileter_list;
		}

		public List<CategoryFilter> getCategoryfileter_list() {
			return categoryfileter_list;
		}

		public void setCategoryfileter_list(List<CategoryFilter> categoryfileter_list) {
			this.categoryfileter_list = categoryfileter_list;
		}
		
	}
}

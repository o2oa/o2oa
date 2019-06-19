package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ActionListWhatICanView_Data extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWhatICanView_Data.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, String appId, EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<String> ids = null;
		List<CategoryInfo> categoryInfoList = null;
		Boolean check = true;
		Boolean isXAdmin = false;
		Boolean appManager = false;
		Boolean appPublisher = false;
		Boolean appViewer = false;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();
		List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
		List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
		
		if ( StringUtils.isEmpty(appId)) {
			check = false;
			Exception exception = new ExceptionAppIdEmpty();
			result.error(exception);
		}

		try {
			isXAdmin = userManagerService.isManager(request, effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCategoryInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {// 判断用户是否该栏目的管理者
			try {
				if ( appInfoServiceAdv.isAppInfoManager( appId, personName )) {
					appManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {// 判断用户是否该栏目的发布者
			try {
				if (appInfoServiceAdv.isAppInfoPublisher( appId, personName, unitNames, groupNames )) {
					appPublisher = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {// 判断用户是否该栏目的访问者
			try {
				if (appInfoServiceAdv.isAppInfoViewer( appId, personName, unitNames, groupNames )) {
					appViewer = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		String cacheKey = ApplicationCache.concreteCacheKey( personName, appId, "Data", isXAdmin, appManager, appPublisher, appViewer );
		Element element = cache.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData(wos);
		} else {
			if (check) {
				if ( isXAdmin || appManager || appPublisher || appViewer ) {
					try {
						ids = categoryInfoServiceAdv.listIdsByAppId( appId );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionCategoryInfoProcess(e, "根据应用栏目ID查询分类信息对象时发生异常。AppId:" + appId);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {
					if (check) {
						try {
							List<String> inAppInfoIds = new ArrayList<>();
							inAppInfoIds.add( appId );			
							ids = permissionQueryService.listViewableCategoryIdByPerson(
									personName, isAnonymous, unitNames, groupNames, inAppInfoIds, null, null, "数据", 1000, isXAdmin );
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionCategoryInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}

			if (check) {
				if ( ids != null && !ids.isEmpty() ) {
					try {
						categoryInfoList = categoryInfoServiceAdv.list(ids);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionCategoryInfoProcess(e, "根据ID列表查询分类信息对象时发生异常。");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}

			if (check) {
				if (categoryInfoList != null && !categoryInfoList.isEmpty()) {
					try {
						wos = Wo.copier.copy( categoryInfoList );
						for(Wo wo : wos) {
							wo.setExtContent( categoryInfoServiceAdv.getExtContentWithId( wo.getId() ));
						}
						SortTools.asc(wos, "categorySeq");
						cache.put(new Element(cacheKey, wos));
						result.setData(wos);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionCategoryInfoProcess(e, "将查询出来的分类信息对象转换为可输出的数据信息时发生异常。");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		return result;
	}
	
	public static class Wo extends CategoryInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo( CategoryInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));
		
		@FieldDescribe("扩展信息JSON内容")
		private String extContent = null;

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}
	}
}
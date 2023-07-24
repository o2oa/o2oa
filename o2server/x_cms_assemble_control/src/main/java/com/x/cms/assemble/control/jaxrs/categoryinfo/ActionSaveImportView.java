package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.query.core.entity.View;

/**
 * 单独更新分类绑定的导入列表ID
 * @author O2LEE
 *
 */
public class ActionSaveImportView extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSaveImportView.class );

	@AuditLog(operation = "修改分类文档导入视图")
	protected ActionResult<Wo> execute(  HttpServletRequest request, EffectivePerson effectivePerson, String categoryId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		View view = null;
		Wi wi = null;
		Boolean check = true;

		if( StringUtils.isEmpty(categoryId) ){
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( categoryId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询分类信息对象时发生异常。ID:" + categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCategoryInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
//		
//		if( StringUtils.isEmpty( wi.getViewAppId() ) ){
//			check = false;
//			Exception exception = new ExceptionViewAppIdEmpty();
//			result.error( exception );
//		}
//		if( StringUtils.isEmpty( wi.getViewId() )  ){
//			check = false;
//			Exception exception = new ExceptionViewIdEmpty();
//			result.error( exception );
//		}
		
//		if( check ){
//			if( StringUtils.isNotEmpty( wi.getViewId() )) {
//				try {
//					view = queryViewService.getQueryView( wi.getViewId() );
//					if( view == null ){
//						check = false;
//						Exception exception = new ExceptionViewInfoNotExists( wi.getViewId() );
//						result.error( exception );
//					}
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询数据视图信息对象时发生异常。VIEWID:" + wi.getViewId() );
//					result.error( exception );
//					logger.error( e, effectivePerson, request, null);
//				}
//			}
//		}
		
		if( check ){
			try {
				categoryInfoServiceAdv.bindImportViewId( categoryInfo, view, wi.getViewAppId() );

				CacheManager.notify(AppInfo.class);
				CacheManager.notify(CategoryInfo.class);
				
				Wo wo = new Wo();
				wo.setId(categoryId);
				result.setData(wo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "为分类信息绑定导入列表ID时发生异常。category ID:" + categoryId + ", bind view ID:" + view.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	public static class Wi {

		@FieldDescribe("数据视图应用ID")
		private String viewAppId = null;
		
		@FieldDescribe("数据视图ID")
		private String viewId = null;

		public String getViewAppId() {
			return viewAppId;
		}

		public String getViewId() {
			return viewId;
		}

		public void setViewAppId(String viewAppId) {
			this.viewAppId = viewAppId;
		}

		public void setViewId(String viewId) {
			this.viewId = viewId;
		}
	}
	
	public static class Wo extends WoId {

	}
}
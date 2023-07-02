package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public class ActionCategoryInfoViewerSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionCategoryInfoViewerSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String categoryId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;		

		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
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
				Exception exception = new ExceptionCategoryInfoQueryById( e, categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				categoryInfoServiceAdv.updateViewerPermission( categoryId, wi.getPersonList(), wi.getUnitList(), wi.getGroupList() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoPermissionSave( e, categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//更新完成
		if( check ){
			String description = "分类："+categoryId+"权限变更";
			new LogService().log( null,  effectivePerson.getDistinguishedName(), description, categoryId, "", "", categoryId, "CATEGORYINFO_VIEWER", "可见范围变更" );

			CacheManager.notify( AppInfo.class );
			CacheManager.notify( CategoryInfo.class );
			
			Wo wo = new Wo();
			wo.setId( categoryId );
			result.setData( wo );
		}
		if( check ) {
			try {
				new CmsBatchOperationPersistService().addOperation( 
						CmsBatchOperationProcessService.OPT_OBJ_CATEGORY, 
						CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  categoryId,  categoryId, "分类可见范围变更：ID=" +  categoryId );
			}catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe("人员列表")
		private List<String> personList;
		
		@FieldDescribe("组织列表")
		private List<String> unitList;
		
		@FieldDescribe("群组列表")
		private List<String> groupList;

		public List<String> getPersonList() {
			return personList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public List<String> getGroupList() {
			return groupList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}		
	}

	public static class Wo extends WoId {

	}
}
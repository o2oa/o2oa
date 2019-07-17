package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;

public class ActionAppInfoManagerSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAppInfoManagerSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		AppInfo appInfo = null;
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
				appInfo = appInfoServiceAdv.get( appId );
				if( appInfo == null ){
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( appId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoQueryById( e, appId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				appInfoServiceAdv.updateManagerPermission( appId, wi.getPersonList(), wi.getUnitList(), wi.getGroupList() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoPermissionSave( e, appId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//更新完成
		if( check ){
			String description = "栏目："+appId+"权限变更";
			new LogService().log( null,  effectivePerson.getDistinguishedName(), description, appId, "", "", appId, "APPINFO_MANAGER", "管理权限变更" );
			ApplicationCache.notify( AppInfo.class );
			Wo wo = new Wo();
			wo.setId( appId );
			result.setData( wo );
		}
		
		if( check ) {
			try {
				new CmsBatchOperationPersistService().addOperation( 
						CmsBatchOperationProcessService.OPT_OBJ_APPINFO, 
						CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  appId,  appId, "栏目管理权限变更：ID=" +  appId );
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
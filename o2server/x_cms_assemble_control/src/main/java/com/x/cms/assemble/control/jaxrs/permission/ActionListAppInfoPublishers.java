package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;

public class ActionListAppInfoPublishers extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAppInfoPublishers.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AppInfo appInfo = null;
		Boolean check = true;

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
			Wo wo = new Wo();
			wo.setPersonList( appInfo.getPublishablePersonList() );
			wo.setUnitList( appInfo.getPublishableUnitList() );
			wo.setGroupList( appInfo.getPublishableGroupList() );
			wo.setRoleList( appInfo.getPublishableRoleList());
			result.setData( wo );
		}
		return result;
	}

	public static class Wo{

		@FieldDescribe("人员列表")
		private List<String> personList;

		@FieldDescribe("组织列表")
		private List<String> unitList;

		@FieldDescribe("群组列表")
		private List<String> groupList;

		@FieldDescribe("角色列表")
		private List<String> roleList;

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

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}
	}
}

package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoIdEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionGet extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		List<WoPermissionInfo> permissionWraps = null;
		BBSRoleInfo roleInfo = null;
		List<BBSPermissionInfo> permissionList = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionRoleInfoIdEmpty();
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在根据ID获取BBS角色信息时发生异常！ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if( roleInfo == null ){
				check = false;
				Exception exception = new ExceptionRoleInfoNotExists( id );
				result.error( exception );
			}
		}
		if (check) {
			// 如果角色信息存在，然后再查询该角色所拥有的所有权限信息
			try {
				permissionList = permissionInfoService.listPermissionByRoleCode(roleInfo.getRoleCode());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在根据角色编码查询权限信息列表时发生异常！Code:" + roleInfo.getRoleCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (ListTools.isNotEmpty(permissionList) ) {
				try {
					permissionWraps = WoPermissionInfo.copier.copy(permissionList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess( e, "系统在转换所有BBS权限信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			try {
				wrap = Wo.copier.copy(roleInfo);
				wrap.setPermissions(permissionWraps);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在转换所有BBS角色信息为输出对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends BBSRoleInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();	
		
		public static WrapCopier< BBSRoleInfo, Wo > copier = WrapCopierFactory.wo( BBSRoleInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private List<WoPermissionInfo> permissions = null;

		public List<WoPermissionInfo> getPermissions() {
			return permissions;
		}

		public void setPermissions(List<WoPermissionInfo> permissions) {
			this.permissions = permissions;
		}
		
	}
	
	public static class WoPermissionInfo extends BBSPermissionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSPermissionInfo, WoPermissionInfo > copier = WrapCopierFactory.wo( BBSPermissionInfo.class, WoPermissionInfo.class, null, JpaObject.FieldsInvisible);
		
		//论坛版块列表
		private List<WoSectionInfo> sections = null;

		public List<WoSectionInfo> getSections() {
			return sections;
		}

		public void setSections(List<WoSectionInfo> sections) {
			this.sections = sections;
		}
	}

	public static class WoSectionInfo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		//版块的子版块信息列表
		private List<WoSectionInfo> subSections = null;

		public List<WoSectionInfo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<WoSectionInfo> subSections) {
			this.subSections = subSections;
		}	
	}

}
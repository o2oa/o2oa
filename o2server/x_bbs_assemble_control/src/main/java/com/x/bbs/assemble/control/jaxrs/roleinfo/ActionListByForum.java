package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionListByForum extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListByForum.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSForumInfo forumInfo = null;
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionRoleInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error( exception );
			}
		}
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + wrapIn.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (forumInfo == null) {
				check = false;
				Exception exception = new ExceptionForumInfoNotExists( wrapIn.getForumId() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByForumId(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在根据论坛分区ID查询角色信息列表时发生异常.Forum:" + wrapIn.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = Wo.copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess( e, "系统在转换所有BBS角色信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wi{
		
		public static List<String> Excludes = new ArrayList<String>();
		
		private String unitName = null;
		
		private String userName = null;
		
		private String forumId = null;
		
		private String sectionId = null;
		
		private String bindRoleCode = null;
		
		private BindObject bindObject = null;
		
		private List<String> bindRoleCodes = null;
		
		private List<BindObject> bindObjectArray = null;

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getForumId() {
			return forumId;
		}

		public void setForumId(String forumId) {
			this.forumId = forumId;
		}

		public String getSectionId() {
			return sectionId;
		}

		public void setSectionId(String sectionId) {
			this.sectionId = sectionId;
		}

		public List<String> getBindRoleCodes() {
			return bindRoleCodes;
		}

		public void setBindRoleCodes(List<String> bindRoleCodes) {
			this.bindRoleCodes = bindRoleCodes;
		}

		public List<BindObject> getBindObjectArray() {
			return bindObjectArray;
		}

		public void setBindObjectArray(List<BindObject> bindObjectArray) {
			this.bindObjectArray = bindObjectArray;
		}

		public String getBindRoleCode() {
			return bindRoleCode;
		}

		public void setBindRoleCode(String bindRoleCode) {
			this.bindRoleCode = bindRoleCode;
		}

		public BindObject getBindObject() {
			return bindObject;
		}

		public void setBindObject(BindObject bindObject) {
			this.bindObject = bindObject;
		}
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
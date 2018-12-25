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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionBindRoleCodeEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSUserRole;

public class ActionLisstBindObjectByRoleCode extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionLisstBindObjectByRoleCode.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<BBSUserRole> userRoleList = null;
		BBSRoleInfo roleInfo = null;
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
			if (wrapIn.getBindRoleCode() == null || wrapIn.getBindRoleCode().isEmpty()) {
				check = false;
				Exception exception = new ExceptionBindRoleCodeEmpty();
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.getByRoleCode( wrapIn.getBindRoleCode() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在根据编码获取BBS角色信息时发生异常！Code:" + wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfo == null) {
				check = false;
				Exception exception = new ExceptionRoleInfoNotExists( wrapIn.getBindRoleCode() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				userRoleList = roleInfoService.listUserRoleByRoleCode(roleInfo.getRoleCode());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "系统在根据角色编码查询角色绑定信息列表时发生异常.Code:" + wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (userRoleList != null) {
				try {
					wraps = Wo.copier.copy( userRoleList );
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

	public static class Wo extends BBSUserRole{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSUserRole, Wo > copier = WrapCopierFactory.wo(BBSUserRole.class, Wo.class, null, JpaObject.FieldsInvisible);
	}


}
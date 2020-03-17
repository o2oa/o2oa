package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendanceadmin.exception.ExceptionAttendanceAdminProcess;
import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceAdmin attendanceAdmin = null;
		String topUnitName = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getUnitName() == null || wrapIn.getUnitName().isEmpty()) {
				try {
					topUnitName = userManagerService.getTopUnitNameWithPersonName( currentPerson.getDistinguishedName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceAdminProcess(e, "系统获取登录用户所属顶层组织时发生异常。Name：" + currentPerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
				wrapIn.setUnitName(topUnitName);
			}
		}
		if (check) {
			try {
				attendanceAdmin = new AttendanceAdmin();
				wrapIn.copyTo( attendanceAdmin, JpaObject.FieldsUnmodify );
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					attendanceAdmin.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAdminProcess(e, "系统在转换所有管理员信息为输出对象时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceAdmin = attendanceAdminServiceAdv.save(attendanceAdmin);
				result.setData(new Wo(attendanceAdmin.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAdminProcess(e, "系统保存管理员信息时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends AttendanceAdmin {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
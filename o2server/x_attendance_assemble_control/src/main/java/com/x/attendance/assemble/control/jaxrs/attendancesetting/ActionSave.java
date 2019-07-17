package com.x.attendance.assemble.control.jaxrs.attendancesetting;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendancesetting.exception.ExceptionAttendanceSettingProcess;
import com.x.attendance.assemble.control.jaxrs.attendancesetting.exception.ExceptionSettingCodeEmpty;
import com.x.attendance.assemble.control.jaxrs.attendancesetting.exception.ExceptionSettingNameEmpty;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		AttendanceSetting attendanceSetting = new AttendanceSetting();
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty()) {
				check = false;
				Exception exception = new ExceptionSettingCodeEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getConfigName() == null || wrapIn.getConfigName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionSettingNameEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceSetting = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					attendanceSetting.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess(e, "系统将用户传入的数据转换为考勤系统配置对象时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceSetting = attendanceSettingServiceAdv.save(attendanceSetting);
				result.setData(new Wo(attendanceSetting.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceSettingProcess(e, "保存考勤系统配置信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceSetting> copier = WrapCopierFactory.wi(Wi.class, AttendanceSetting.class,
				null, JpaObject.FieldsUnmodify);
		
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

	public static class Wo extends WoId {
		public Wo(String id) {
			setId(id);
		}
	}
}
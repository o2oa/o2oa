package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig.exception.ExceptionAttendanceEmployeeProcess;
import com.x.attendance.entity.AttendanceEmployeeConfig;
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
		AttendanceEmployeeConfig attendanceEmployeeConfig = new AttendanceEmployeeConfig();
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
			try {
				attendanceEmployeeConfig = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					attendanceEmployeeConfig.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "将传入的参数转换为人员考勤配置对象信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.save(attendanceEmployeeConfig);
				result.setData(new Wo(attendanceEmployeeConfig.getId()));
				logger.info("人员考勤配置数据保存成功！");
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "系统保存人员考勤配置对象信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceEmployeeConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceEmployeeConfig> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceEmployeeConfig.class, null, JpaObject.FieldsUnmodify);

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
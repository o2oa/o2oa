package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionConfigIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get(id);
				if (attendanceEmployeeConfig == null) {
					check = false;
					Exception exception = new ExceptionConfigNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "系统根据ID查询指定的人员考勤配置信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				wrap = Wo.copier.copy(attendanceEmployeeConfig);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceEmployeeConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceEmployeeConfig, Wo> copier = WrapCopierFactory
				.wo(AttendanceEmployeeConfig.class, Wo.class, null, JpaObject.FieldsInvisible);
	}

}
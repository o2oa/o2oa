package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGetMobile extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetMobile.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Wo wrap = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		Boolean check = true;

		if (check) {
			if (id == null) {
				check = false;
				Exception exception = new ExceptionDetailMobileIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.getMobile(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在根据ID查询员工手机打卡信息时发生异常！ID:" + id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (attendanceDetailMobile == null) {
				check = false;
				Exception exception = new ExceptionDetaillMobileNotExists(id);
				result.error(exception);
			}
		}
		if (check) {
			try {
				wrap = Wo.copier.copy(attendanceDetailMobile);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工手机打卡信息为输出对象时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, Wo> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				Wo.class, null, JpaObject.FieldsInvisible);
	}

}
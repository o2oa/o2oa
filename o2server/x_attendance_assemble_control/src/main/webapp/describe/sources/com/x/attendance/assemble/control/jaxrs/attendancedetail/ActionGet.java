package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionAttendanceDetailProcess;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionDetailIdEmpty;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionDetailNotExists;
import com.x.attendance.entity.AttendanceDetail;
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		Wo wrap = null;
		AttendanceDetail attendanceDetail = null;
		Boolean check = true;

		if (check) {
			if (id == null) {
				check = false;
				Exception exception = new ExceptionDetailIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在根据ID查询员工打卡信息时发生异常！ID:" + id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (attendanceDetail == null) {
				check = false;
				Exception exception = new ExceptionDetailNotExists(id);
				result.error(exception);
			}
		}
		if (check) {
			try {
				wrap = Wo.copier.copy(attendanceDetail);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工打卡信息为输出对象时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceDetail {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetail, Wo> copier = WrapCopierFactory.wo(AttendanceDetail.class, Wo.class,
				null, JpaObject.FieldsInvisible);
	}

}
package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.jaxrs.attendanceadmin.exception.ExceptionAttendanceAdminProcess;
import com.x.attendance.entity.AttendanceAdmin;
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
		AttendanceAdmin attendanceAdmin = null;
		Boolean check = true;
		if (check) {
			try {
				attendanceAdmin = attendanceAdminServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new ExceptionAttendanceAdminProcess(e, "系统在根据ID获取管理员信息时发生异常！ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (attendanceAdmin != null) {
				try {
					wrap = Wo.copier.copy(attendanceAdmin);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceAdminProcess(e, "系统在转换所有管理员信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wrap);
		return result;
	}

	public static class Wo extends AttendanceAdmin {

		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<AttendanceAdmin, Wo> copier = WrapCopierFactory.wo(AttendanceAdmin.class, Wo.class,
				null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}
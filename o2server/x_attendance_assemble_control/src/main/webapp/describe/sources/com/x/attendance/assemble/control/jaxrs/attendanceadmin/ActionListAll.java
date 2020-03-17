package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import java.util.ArrayList;
import java.util.List;

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
import com.x.base.core.project.tools.ListTools;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListAll.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<AttendanceAdmin> attendanceAdminList = null;
		Boolean check = true;

		if (check) {
			try {
				attendanceAdminList = attendanceAdminServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAdminProcess(e, "系统在获取所有管理员信息时发生异常");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( ListTools.isNotEmpty( attendanceAdminList )) {
				try {
					wraps = Wo.copier.copy(attendanceAdminList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceAdminProcess(e, "系统在转换所有管理员信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceAdmin {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

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
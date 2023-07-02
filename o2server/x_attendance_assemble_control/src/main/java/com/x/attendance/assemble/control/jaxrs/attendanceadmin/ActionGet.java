package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Wo wrap = null;
		AttendanceAdmin attendanceAdmin = attendanceAdminServiceAdv.get(id);
		if (attendanceAdmin != null) {
			wrap = Wo.copier.copy(attendanceAdmin);
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

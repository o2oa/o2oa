package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListImportByFileName extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListImportByFileName.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			String file_id) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Boolean check = true;

		if (check) {
			if (file_id == null) {
				check = false;
				Exception exception = new ExceptionFileIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				ids = attendanceDetailServiceAdv.listByBatchName(file_id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"系统在根据打卡信息导入文件ID查询员工打卡信息时发生异常！FileId:" + file_id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (ids != null && !ids.isEmpty()) {
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"系统根据开始时间和结束时间查询需要分析的员工打卡信息ID列表时发生异常！");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (attendanceDetailList != null && !attendanceDetailList.isEmpty()) {
				try {
					wraps = Wo.copier.copy(attendanceDetailList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工打卡信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceDetail {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetail, Wo> copier = WrapCopierFactory.wo(AttendanceDetail.class, Wo.class,
				null, JpaObject.FieldsInvisible);
	}
}
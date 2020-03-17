package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionAttendanceDetailProcess;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<Wo> wraps = new ArrayList<>();
		String q_empName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Date maxRecordDate = null;
		String maxRecordDateString = null;
		DateOperation dateOperation = new DateOperation();
		Wi wrapIn = null;
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
			if (wrapIn == null) {
				wrapIn = new Wi();
			}
			q_empName = wrapIn.getQ_empName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
		}
		if (check) {
			try {
				maxRecordDateString = attendanceDetailServiceAdv.getMaxRecordDate();
				maxRecordDate = dateOperation.getDateFromString(maxRecordDateString);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在查询打卡信息记录最大日期时发生异常.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (q_year == null || q_year.isEmpty()) {
				q_year = dateOperation.getYear(maxRecordDate);
			}
			if (q_month == null || q_month.isEmpty()) {
				q_month = dateOperation.getMonth(maxRecordDate);
			}
		}
		if (check) {
			try {
				ids = attendanceDetailServiceAdv.listUserAttendanceDetailByYearAndMonth(q_empName, q_year, q_month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在根据员工姓名，年份月份查询打卡详细信息ID列表时发生异常！"
						+ "Name:" + q_empName + ", Year:" + q_year + ", Month:" + q_month);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
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
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			if (attendanceDetailList != null) {
				try {
					wraps = Wo.copier.copy(attendanceDetailList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工打卡信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe( "用于查询的人员：DistinguishedName." )
		private String q_empName;

		@FieldDescribe( "查询的年份." )
		private String q_year;

		@FieldDescribe( "查询的月份." )
		private String q_month;

		public String getQ_empName() {
			return q_empName;
		}

		public String getQ_year() {
			return q_year;
		}

		public String getQ_month() {
			return q_month;
		}

		public void setQ_empName(String q_empName) {
			this.q_empName = q_empName;
		}

		public void setQ_year(String q_year) {
			this.q_year = q_year;
		}

		public void setQ_month(String q_month) {
			this.q_month = q_month;
		}
	}

	public static class Wo extends AttendanceDetail {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetail, Wo> copier = WrapCopierFactory.wo(AttendanceDetail.class, Wo.class,
				null, JpaObject.FieldsInvisible);
	}
}
package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionAttendanceDetailProcess;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListWithTopUnit extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWithTopUnit.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<Wo> wraps = new ArrayList<>();
		String q_topUnitName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		List<String> topUnitNames = new ArrayList<String>();
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
			q_topUnitName = wrapIn.getQ_topUnitName();
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
			if ( StringUtils.isNotEmpty( q_topUnitName )) {
				try {
					topUnitNames = userManagerService.listSubUnitNameWithParent(q_topUnitName);
				} catch (Exception e) {
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"根据顶层组织顶层组织列示所有下级组织名称发生异常！TopUnit:" + q_topUnitName);
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
				if (!topUnitNames.contains(q_topUnitName)) {
					topUnitNames.add(q_topUnitName);
				}
			}
		}
		if (check) {
			try {
				ids = attendanceDetailServiceAdv.listTopUnitAttendanceDetailByYearAndMonth(topUnitNames, q_year,
						q_month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在根据顶层组织名称，年份、月份查询打卡详细信息ID列表时发生异常！"
						+ "TopUnit:" + topUnitNames + ", Year:" + q_year + ", Month:" + q_month);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceDetailList = attendanceDetailServiceAdv.list(ids);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统根据开始时间和结束时间查询需要分析的员工打卡信息ID列表时发生异常！");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
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

		private String q_empName;

		private List<String> topUnitNames;

		private String q_topUnitName;

		private List<String> unitNames;

		private String q_unitName;

		private String q_year;

		private String q_month;

		private String cycleYear;

		private String cycleMonth;

		private String q_date;

		private int recordStatus = 999;

		private Boolean isAbsent = null;

		private Boolean isLate = null;

		private Boolean isLeaveEarlier = null;

		private Boolean isLackOfTime = null;

		private String order = "DESC";

		private String key;

		public String getQ_empName() {
			return q_empName;
		}

		public String getCycleYear() {
			return cycleYear;
		}

		public void setCycleYear(String cycleYear) {
			this.cycleYear = cycleYear;
		}

		public String getCycleMonth() {
			return cycleMonth;
		}

		public void setCycleMonth(String cycleMonth) {
			this.cycleMonth = cycleMonth;
		}

		public void setQ_empName(String q_empName) {
			this.q_empName = q_empName;
		}

		public String getQ_year() {
			return q_year;
		}

		public void setQ_year(String q_year) {
			this.q_year = q_year;
		}

		public String getQ_month() {
			return q_month;
		}

		public void setQ_month(String q_month) {
			this.q_month = q_month;
		}

		public List<String> getTopUnitNames() {
			return topUnitNames;
		}

		public void setTopUnitNames(List<String> topUnitNames) {
			this.topUnitNames = topUnitNames;
		}

		public List<String> getUnitNames() {
			return unitNames;
		}

		public void setUnitNames(List<String> unitNames) {
			this.unitNames = unitNames;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getQ_date() {
			return q_date;
		}

		public void setQ_date(String q_date) {
			this.q_date = q_date;
		}

		public Boolean getIsAbsent() {
			return isAbsent;
		}

		public void setIsAbsent(Boolean isAbsent) {
			this.isAbsent = isAbsent;
		}

		public Boolean getIsLate() {
			return isLate;
		}

		public void setIsLate(Boolean isLate) {
			this.isLate = isLate;
		}

		public Boolean getIsLeaveEarlier() {
			return isLeaveEarlier;
		}

		public void setIsLeaveEarlier(Boolean isLeaveEarlier) {
			this.isLeaveEarlier = isLeaveEarlier;
		}

		public Boolean getIsLackOfTime() {
			return isLackOfTime;
		}

		public void setIsLackOfTime(Boolean isLackOfTime) {
			this.isLackOfTime = isLackOfTime;
		}

		public int getRecordStatus() {
			return recordStatus;
		}

		public void setRecordStatus(int recordStatus) {
			this.recordStatus = recordStatus;
		}

		public String getQ_topUnitName() {
			return q_topUnitName;
		}

		public void setQ_topUnitName(String q_topUnitName) {
			this.q_topUnitName = q_topUnitName;
		}

		public String getQ_unitName() {
			return q_unitName;
		}

		public void setQ_unitName(String q_unitName) {
			this.q_unitName = q_unitName;
		}

	}

	public static class Wo extends AttendanceDetail {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetail, Wo> copier = WrapCopierFactory.wo(AttendanceDetail.class, Wo.class,
				null, JpaObject.FieldsInvisible);
	}
}
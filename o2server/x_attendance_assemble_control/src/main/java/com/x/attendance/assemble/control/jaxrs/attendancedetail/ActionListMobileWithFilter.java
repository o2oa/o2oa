package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListMobileWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListMobileWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<Wo> wraps = new ArrayList<>();
		List<Wo> allResultWrap = null;
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		Long total = 0L;
		Integer selectTotal = 0;
		Boolean check = true;
		Wi wrapIn = null;
		Boolean queryConditionIsNull = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if (check) {
			if (page == null) {
				page = 1;
			}
		}
		if (check) {
			if (count == null) {
				count = 20;
			}
		}
		if (page <= 0) {
			page = 1;
		}
		if (count <= 0) {
			count = 20;
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getEmpNo() )) {
				queryConditionIsNull = false;
			}
			if ( StringUtils.isNotEmpty( wrapIn.getEmpName() )) {
				queryConditionIsNull = false;
			}
			if ( StringUtils.isNotEmpty( wrapIn.getStartDate() )) {
				queryConditionIsNull = false;
				if ( StringUtils.isEmpty( wrapIn.getEndDate() )) {
					wrapIn.setEndDate(wrapIn.getStartDate());
				}
			}
			if (queryConditionIsNull) {
				check = false;
				Exception exception = new ExceptionQueryParameterEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getEndDate() )) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getEndDate());
					wrapIn.setEndDate(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD")); // 结束日期
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"查询结束日期格式异常，格式：yyyy-mm-dd.日期：" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
					// 错误的格式 清空 没有endDate 只查询startDate当天数据
					if ( StringUtils.isNotEmpty( wrapIn.getEndDate() )) {
						wrapIn.setEndDate(null);
					}
				}

			}
			if ( StringUtils.isNotEmpty( wrapIn.getStartDate() )) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getStartDate());
					wrapIn.setStartDate(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD")); // 开始日期
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"查询开始日期格式异常，格式：yyyy-mm-dd.日期：" + wrapIn.getStartDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		// 查询的最大条目数
		selectTotal = page * count;
		if (check) {
			if (selectTotal > 0) {
				try {
					total = attendanceDetailServiceAdv.countAttendanceDetailMobileForPage(wrapIn.getEmpNo(),
							wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(),
							wrapIn.getEndDate());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"根据条件查询员工手机打卡信息条目数时发生异常." + "EmpNo:" + wrapIn.getEmpNo() + ", EmpName:"
									+ wrapIn.getEmpName() + ", SignDescription:" + wrapIn.getSignDescription()
									+ ", StartDate:" + wrapIn.getStartDate() + ", EndDate:" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			if (selectTotal > 0 && total > 0) {
				try {
					attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobileForPage(
							wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(),
							wrapIn.getEndDate(), selectTotal);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"根据条件查询员工手机打卡信息列表时发生异常." + "EmpNo:" + wrapIn.getEmpNo() + ", EmpName:" + wrapIn.getEmpName()
									+ ", SignDescription:" + wrapIn.getSignDescription() + ", StartDate:"
									+ wrapIn.getStartDate() + ", EndDate:" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			if (attendanceDetailMobileList != null && !attendanceDetailMobileList.isEmpty()) {
				try {
					allResultWrap = Wo.copier.copy(attendanceDetailMobileList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工手机打卡信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			int startIndex = (page - 1) * count;
			int endIndex = page * count;
			for (int i = 0; allResultWrap != null && i < allResultWrap.size(); i++) {
				if (i >= startIndex && i < endIndex) {
					wraps.add(allResultWrap.get(i));
				}
			}
		}
		result.setCount(total);
		result.setData(wraps);

		return result;
	}

	public static class Wi {

		@FieldDescribe("员工号，根据员工号查询记录")
		private String empNo;

		@FieldDescribe("员工姓名，根据员工姓名查询记录.")
		private String empName;

		@FieldDescribe("开始日期：yyyy-mm-dd.")
		private String startDate;

		@FieldDescribe("结束日期：yyyy-mm-dd,如果开始日期填写，结束日期不填写就是只查询开始日期那一天")
		private String endDate;

		@FieldDescribe("打卡说明:上班打卡，下班打卡.")
		private String signDescription;

		public String getEmpNo() {
			return empNo;
		}

		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}

		public String getEmpName() {
			return empName;
		}

		public void setEmpName(String empName) {
			this.empName = empName;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getSignDescription() {
			return signDescription;
		}

		public void setSignDescription(String signDescription) {
			this.signDescription = signDescription;
		}

	}

	public static class Wo extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, Wo> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				Wo.class, null, JpaObject.FieldsInvisible);
	}
}
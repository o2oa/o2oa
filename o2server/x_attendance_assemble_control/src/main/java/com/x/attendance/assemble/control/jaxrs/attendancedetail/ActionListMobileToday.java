package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActionListMobileToday extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListMobileToday.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
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
		AttendanceScheduleSetting scheduleSetting = null;
		Boolean queryConditionIsNull = true;

		if (check) {
			scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( effectivePerson.getDistinguishedName(), effectivePerson.getDebugger() );
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
				}
				if ( StringUtils.isNotEmpty( wrapIn.getEndDate() )) {
					wrapIn.setEndDate(wrapIn.getStartDate());
				}
			}

			if ( StringUtils.isNotEmpty( wrapIn.getStartDate() )) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getStartDate());
					wrapIn.setStartDate(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD")); // 开始日期
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"查询开始日期格式异常，格式：yyyy-mm-dd.日期：" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			try {
				attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobile( effectivePerson.getDistinguishedName(), wrapIn.getDate() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"根据条件查询员工手机打卡信息列表时发生异常.DistinguishedName:" + effectivePerson.getDistinguishedName() + ",Date:" + wrapIn.getDate());
				result.error(exception);
				logger.error(e, currentPerson, request, null);
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

		@FieldDescribe("员工标识：DistinguishedName.")
		private String person;

		@FieldDescribe("打卡日期：yyyy-mm-dd.")
		private String signDate;

		@FieldDescribe("下一次打卡描述")
		private WoSignFeature nextSign;

		public String getPerson() { return person; }

		public void setPerson(String person) { this.person = person; }

		public String getSignDate() { return signDate; }

		public void setSignDate(String signDate) { this.signDate = signDate; }

		public WoSignFeature getNextSign() { return nextSign; }

		public void setNextSign(WoSignFeature nextSign) { this.nextSign = nextSign; }
	}

	public static class Wo extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, Wo> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				Wo.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoSignFeature{

		@FieldDescribe("打卡次数.")
		private Integer signSeq;

		@FieldDescribe("打卡日期：yyyy-mm-dd.")
		private String signDate;

		@FieldDescribe("打卡操作名称：上班打卡|下班打卡|午休打卡|上午下班打卡|下午上班打卡(根据不同的打卡策略稍有不同)....")
		private String signName;

		@FieldDescribe("最晚打卡时间")
		private Date latestSignTime;

		public Integer getSignSeq() { return signSeq; }

		public void setSignSeq(Integer signSeq) { this.signSeq = signSeq; }

		public String getSignDate() { return signDate; }

		public void setSignDate(String signDate) { this.signDate = signDate; }

		public String getSignName() { return signName; }

		public void setSignName(String signName) { this.signName = signName; }

		public Date getLatestSignTime() { return latestSignTime; }

		public void setLatestSignTime(Date latestSignTime) { this.latestSignTime = latestSignTime; }

	}
}
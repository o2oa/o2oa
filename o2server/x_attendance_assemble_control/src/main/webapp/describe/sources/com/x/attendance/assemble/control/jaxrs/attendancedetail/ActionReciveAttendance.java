package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionAttendanceDetailProcess;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionPersonNameEmpty;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionRecordDateEmpty;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionReciveAttendance extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionReciveAttendance.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Date datetime = null;
		List<String> ids_temp = null;
		DateOperation dateOperation = new DateOperation();
		AttendanceDetail attendanceDetail = new AttendanceDetail();
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		List<AttendanceSelfHoliday> selfHolidays = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (wrapIn.getRecordDateString() == null || wrapIn.getRecordDateString().isEmpty()) {
				check = false;
				Exception exception = new ExceptionRecordDateEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getEmpName() == null || wrapIn.getEmpName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionPersonNameEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				datetime = dateOperation.getDateFromString(wrapIn.getRecordDateString());
				attendanceDetail.setRecordDate(datetime);
				attendanceDetail.setRecordDateString(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD"));
				attendanceDetail.setYearString(dateOperation.getYear(datetime));
				attendanceDetail.setMonthString(dateOperation.getMonth(datetime));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "员工打卡信息中打卡日期格式异常，格式: yyyy-mm-dd. 日期：" + wrapIn.getRecordDateString());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (wrapIn.getOnDutyTime() != null && wrapIn.getOnDutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getOnDutyTime());
					attendanceDetail.setOnDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 上班打卡时间
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "员工上班打卡时间格式异常，格式: HH:mm:ss. 日期：" + wrapIn.getOnDutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (wrapIn.getOffDutyTime() != null && wrapIn.getOffDutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getOffDutyTime());
					attendanceDetail.setOffDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 上班打卡时间
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "员工下班打卡时间格式异常，格式: HH:mm:ss. 日期：" + wrapIn.getOffDutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.save(attendanceDetail);
				result.setData( new Wo( attendanceDetail.getId() ));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在保存员工打卡信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在根据ID列表查询工作节假日配置信息列表时发生异常！" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询并且组织所有的统计周期时发生异常." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try{
				ids_temp = attendanceSelfHolidayServiceAdv.getByPersonName( attendanceDetail.getEmpName() );
				if( ids_temp != null && !ids_temp.isEmpty() ) {
					selfHolidays = attendanceSelfHolidayServiceAdv.list( ids_temp );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "system list attendance self holiday info ids with employee name got an exception.empname:" + attendanceDetail.getEmpName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try{
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( attendanceDetail.getEmpName(), effectivePerson.getDebugger() );
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "system get unit schedule setting for employee with unit names got an exception." + attendanceDetail.getEmpName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( attendanceDetail, attendanceScheduleSetting, selfHolidays, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, effectivePerson.getDebugger());
				logger.info("打卡信息保存并且分析完成。");
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统分析员工打卡信息时发生异常！ID:" + attendanceDetail.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {

		private String empName = null;
		
		private String empNo = null;
		
		private String recordDateString = null;
		
		private String onDutyTime = null;
		
		private String offDutyTime = null;
		
		public String getEmpName() {
			return empName;
		}
		public void setEmpName(String empName) {
			this.empName = empName;
		}
		public String getEmpNo() {
			return empNo;
		}
		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}
		public String getRecordDateString() {
			return recordDateString;
		}
		public void setRecordDateString(String recordDateString) {
			this.recordDateString = recordDateString;
		}
		public String getOnDutyTime() {
			return onDutyTime;
		}
		public void setOnDutyTime(String onDutyTime) {
			this.onDutyTime = onDutyTime;
		}
		public String getOffDutyTime() {
			return offDutyTime;
		}
		public void setOffDutyTime(String offDutyTime) {
			this.offDutyTime = offDutyTime;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}
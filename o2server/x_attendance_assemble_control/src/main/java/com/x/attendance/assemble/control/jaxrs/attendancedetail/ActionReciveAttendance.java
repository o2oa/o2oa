package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;

public class ActionReciveAttendance extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionReciveAttendance.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		DateOperation dateOperation = new DateOperation();
		AttendanceDetail attendanceDetail = new AttendanceDetail();
//		List<String> ids_temp = null;
//		AttendanceScheduleSetting attendanceScheduleSetting = null;
//		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
//		List<AttendanceSelfHoliday> selfHolidays = null;
//		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		Boolean check = true;

		Wi wrapIn = null;
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

		Date datetime = null;

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
			if (wrapIn.getMorningOffdutyTime() != null && wrapIn.getMorningOffdutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getMorningOffdutyTime());
					attendanceDetail.setMorningOffDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 上午下班打卡时间
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "员工上午下班打卡时间格式异常，格式: HH:mm:ss. 日期：" + wrapIn.getMorningOffdutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			if (wrapIn.getAfternoonOnDutyTime() != null && wrapIn.getAfternoonOnDutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getAfternoonOnDutyTime());
					attendanceDetail.setAfternoonOnDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 下午上班打卡时间
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "员工下午上班打卡时间格式异常，格式: HH:mm:ss. 日期：" + wrapIn.getAfternoonOnDutyTime());
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

		if( check ){
			String distinguishedName = wrapIn.getEmpName();
			if( StringUtils.isEmpty( distinguishedName )){
				distinguishedName = effectivePerson.getDistinguishedName();
			}

			Person person = userManagerService.getPersonObjByName( distinguishedName );

			if( person != null ){
				attendanceDetail.setEmpName( person.getDistinguishedName() );
				if( StringUtils.isEmpty( wrapIn.getEmpNo() )){
					if( person != null ){
						if( StringUtils.isNotEmpty( person.getEmployee() )){
							attendanceDetail.setEmpNo(person.getEmployee());
						}else{
							attendanceDetail.setEmpNo( distinguishedName );
						}
					}
				}
			}else{
				//人员不存在
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(
						"考勤人员不存在.DistinguishedName:" + distinguishedName );
				result.error(exception);
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
			//分析保存好的考勤数据
			try {
				ThisApplication.detailAnalyseQueue.send( attendanceDetail.getId() );
			} catch ( Exception e1 ) {
				e1.printStackTrace();
			}
		}

//		if (check) {
//			try {
//				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
//			} catch (Exception e) {
//				check = false;
//				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在根据ID列表查询工作节假日配置信息列表时发生异常！" );
//				result.error(exception);
//				logger.error(e, effectivePerson, request, null);
//			}
//		}
//		if (check) {
//			try {
//				topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
//			} catch (Exception e) {
//				check = false;
//				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询并且组织所有的统计周期时发生异常." );
//				result.error(exception);
//				logger.error(e, effectivePerson, request, null);
//			}
//		}
//
//		if (check) {
//			try{
//				ids_temp = attendanceSelfHolidayServiceAdv.getByPersonName( attendanceDetail.getEmpName() );
//				if( ids_temp != null && !ids_temp.isEmpty() ) {
//					selfHolidays = attendanceSelfHolidayServiceAdv.list( ids_temp );
//				}
//			}catch( Exception e ){
//				check = false;
//				Exception exception = new ExceptionAttendanceDetailProcess( e, "system list attendance self holiday info ids with employee name got an exception.empname:" + attendanceDetail.getEmpName() );
//				result.error(exception);
//				logger.error(e, effectivePerson, request, null);
//			}
//		}
//
//		if (check) {
//			try{
//				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( attendanceDetail.getEmpName(), effectivePerson.getDebugger() );
//			}catch( Exception e ){
//				check = false;
//				Exception exception = new ExceptionAttendanceDetailProcess( e, "system get unit schedule setting for employee with unit names got an exception." + attendanceDetail.getEmpName() );
//				result.error(exception);
//				logger.error(e, effectivePerson, request, null);
//			}
//		}
//
//		if (check) {
//			try {
//				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( attendanceDetail, attendanceScheduleSetting, selfHolidays, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, effectivePerson.getDebugger());
//				logger.info("打卡信息保存并且分析完成。");
//			} catch (Exception e) {
//				check = false;
//				Exception exception = new ExceptionAttendanceDetailProcess(e, "系统分析员工打卡信息时发生异常！ID:" + attendanceDetail.getId());
//				result.error(exception);
//				logger.error(e, effectivePerson, request, null);
//			}
//		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe( "员工标识，<font color='red'>必填</font>，员工的distinguishedName." )
		private String empName = null;

		@FieldDescribe( "员工号, 可以为空，默认为员工标识相同值." )
		private String empNo = null;

		@FieldDescribe( "打卡日期，格式yyyy-mm-dd，可以为空." )
		private String recordDateString = null;

		@FieldDescribe( "上午上班打卡时间，格式hh24:mi:ss，可以为空，为空就是未打卡." )
		private String onDutyTime = null;

		@FieldDescribe("上班下午打卡签退时间，打卡策略2和3时使用，格式hh24:mi:ss，可以为空，为空就是未打卡.")
		private String morningOffdutyTime;

		@FieldDescribe("下午上班打卡签到时间，打卡策略2和3时使用，格式hh24:mi:ss，可以为空，为空就是未打卡.")
		private String afternoonOnDutyTime;

		@FieldDescribe( "下午下班打卡时间，hh24:mi:ss，可以为空，为空就是未打卡." )
		private String offDutyTime = null;

		public String getMorningOffdutyTime() { return morningOffdutyTime; }
		public void setMorningOffdutyTime(String morningOffdutyTime) { this.morningOffdutyTime = morningOffdutyTime; }
		public String getAfternoonOnDutyTime() { return afternoonOnDutyTime; }
		public void setAfternoonOnDutyTime(String afternoonOnDutyTime) { this.afternoonOnDutyTime = afternoonOnDutyTime; }
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
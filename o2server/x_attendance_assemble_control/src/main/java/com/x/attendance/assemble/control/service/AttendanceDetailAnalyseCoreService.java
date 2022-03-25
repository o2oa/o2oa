package com.x.attendance.assemble.control.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
class AttendanceDetailAnalyseCoreService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseCoreService.class );
	private AttendanceSelfHolidayService attendanceSelfHolidayService = new AttendanceSelfHolidayService();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();
	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();
	private static DateOperation dateOperation = new DateOperation();
	private UserManagerService userManagerService = new UserManagerService();

	/**
	 * 打卡记录分析核心方法，分析一条打卡记录信息<br/>
		<b>迟到</b>：晚于最迟“迟到起算时间”，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>早退</b>：下班打卡时间早于“迟到起算时间”，并且工作当日时间不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>异常</b>：缺少签到退中的1条打卡数据 或者 上下班打卡时间都在最迟起算时间内，不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>缺勤</b>：当天没有打卡数据，并且当天是工作日（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）
	 * @param detail
	 * @param scheduleSetting
	 * @throws Exception 
	 */
	AttendanceDetail analyseCore( AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger ) throws Exception {
		if( dateOperation == null ){
			dateOperation = new DateOperation();
		}
		if( detail == null ){
			throw new Exception("detail is null!" );
		}
		if( scheduleSetting == null ){
			throw new Exception("scheduleSetting is null, empName:" + detail.getEmpName() );
		}
		//根据考勤打卡规则来判断启用何种规则来进行考勤结果分析
		if( 2 == scheduleSetting.getSignProxy() ){
			//2、一天三次打卡：打上班，下班两次卡外，中午休息时间也需要打一次卡，以确保员工在公司活动
			detail = new AttendanceDetailAnalyseSignProxy2().analyse(detail, scheduleSetting, debugger);
		}else if( 3 == scheduleSetting.getSignProxy() ){
			//3、一天四次打卡：打上午上班，上午下班，下午上班，下午下班四次卡
			detail = new AttendanceDetailAnalyseSignProxy3().analyse(detail, scheduleSetting, debugger);
		}else{
			//1、一天只打上下班两次卡
			detail = new AttendanceDetailAnalyseSignProxy1().analyse(detail, scheduleSetting, debugger);
		}
		return detail;
	}

	static Date getAfternoonOndutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		if( StringUtils.isNotEmpty( detail.getAfternoonOnDutyTime()) ){
			try {
				logger.debug( debugger, "格式化[下午上班签到时间]afternoonOndutyTime=" +  detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,afternoonOndutyTime=" + detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
				logger.warn( "系统进行时间转换时发生异常,afternoonOndutyTime=" + detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>上午下班签退时间为空！");
		}
		return null;
	}

	static Date getMorningOffdutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		if( StringUtils.isNotEmpty( detail.getMorningOffDutyTime()) ){
			try {
				logger.debug( debugger, "格式化[上午下班签退时间]morningOffdutyTime=" +  detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,morningOffdutyTime=" + detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
				logger.warn( "系统进行时间转换时发生异常,morningOffdutyTime=" + detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>上午下班签退时间为空！");
		}
		return null;
	}

	static Date getAbsenceStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		if( StringUtils.isNotEmpty( scheduleSetting.getAbsenceStartTime() ) ){
			try {
				logger.debug( debugger, ">>>>>>>>>>格式化[缺勤起算时间]absenceStartTime=" +  detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,absenceStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
				logger.warn( "系统进行时间转换时发生异常,absenceStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>上午缺勤时间设置为空！系统将不判上午缺勤情况");
		}
		return null;
	}

	static Date getLeaveEarlyStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		if( StringUtils.isNotEmpty( scheduleSetting.getLeaveEarlyStartTime() ) ){
			try {
				logger.debug( debugger, ">>>>>>>>>>格式化[早退起算时间]leaveEarlyStartTime=" +  detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
				logger.warn( "系统进行时间转换时发生异常,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>早退时间设置为空！系统将不判断早退情况");
		}
		return null;
	}

	static Date getLateStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		if( StringUtils.isNotEmpty( scheduleSetting.getLateStartTime() ) ){
			try {
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + scheduleSetting.getLateStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,lateStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLateStartTime() );
				logger.warn( "系统进行时间转换时发生异常,lateStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLateStartTime());
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>迟到时间设置为空！系统将不判断迟到情况");
		}
		return null;
	}


	/**
	 * 根据已经完善过的考勤打卡记录，获取当天的上午下班（中午休息开始）打卡时间
	 * @param detail
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 */
	static Date getMiddleRestStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		try {
			if( scheduleSetting.getMiddayRestStartTime() != null ){
				logger.debug(debugger, "middleRestStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime());
				return dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestStartTime());
			}
		} catch (Exception e) {
			detail.setDescription(detail.getDescription() + "; 系统进行时间转换时发生异常,middleRestStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.debug(debugger, "系统进行时间转换时发生异常,middleRestStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 根据已经完善过的考勤打卡记录，获取当天的下午上班（中午休息结束）打卡时间
	 * @param detail
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 */
	static Date getMiddleRestEndTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		try {
			if( StringUtils.isNotEmpty(scheduleSetting.getMiddayRestEndTime())){
				logger.debug(debugger, "middleRestEndTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestEndTime());
				return dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestEndTime());
			}
		} catch (Exception e) {
			detail.setDescription(detail.getDescription() + "; 系统进行时间转换时发生异常,middleRestEndTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.debug(debugger, "系统进行时间转换时发生异常,middleRestEndTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 根据已经完善过的考勤打卡记录，获取当天的上班打卡时间
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOnWorkTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOnWorkTime() ) ){
				logger.debug( debugger, "onWorkTime " );
			}else{
				logger.debug( debugger, "onWorkTime=" +  detail.getRecordDateString() + " " + detail.getOnWorkTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			logger.debug( debugger, "系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 根据已经完善过的考勤打卡记录，获取当天的上班打卡时间
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOffWorkTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOffWorkTime() ) ){
				logger.debug( debugger, "offWorkTime为空 " );
			}else{
				logger.debug( debugger, "offWorkTime=" +  detail.getRecordDateString() + " " + detail.getOffWorkTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			logger.debug( debugger, "系统进行时间转换时发生异常,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			logger.error(e);
		}
		return null;
	}

	/**
	 * 根据已经完善过的考勤打卡记录，获取当天的上班实际签到时间
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOnDutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOnDutyTime() ) ){
				logger.debug( debugger, "onDutyTime为空 " );
			}else{
				logger.debug( debugger, "onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnDutyTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
			logger.debug( debugger, "系统进行时间转换时发生异常,onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime());
			logger.error(e);
		}
		return null;
	}
	/**
	 * 根据已经完善过的考勤打卡记录，获取当天的上班实际签退时间
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOffDutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOffDutyTime() ) ){
				logger.debug( debugger, "offDutyTime " );
			}else{
				logger.debug( debugger, "offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffDutyTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
			logger.debug( debugger, "系统进行时间转换时发生异常,offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 分析打卡信息，为打卡数据增加缺勤天数信息，默认为0，每次递增0.5天
	 * @param detail
	 */
	static void increaseAbsenceStatusForAttendanceDetail(AttendanceDetail detail) {
		Double absence = detail.getAbsence();
		if (absence < 1.0) {
			detail.setAbsence( absence + 0.5 );
		}
	}

	//

	/**
	 * 分析打卡信息，为打卡数据计算出勤天数信息，默认为1，每次递减0.5天
	 * @param detail
	 */
	static void increaseAttendanceStatusForAttendanceDetail(AttendanceDetail detail) {
		Double attendance = detail.getAttendance();
		if (attendance > 0.0) {
			detail.setAttendance(attendance - 0.5);
		}
	}

}

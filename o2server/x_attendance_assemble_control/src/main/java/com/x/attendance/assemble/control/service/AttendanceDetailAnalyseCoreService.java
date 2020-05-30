package com.x.attendance.assemble.control.service;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.AttendanceCycles;
import com.x.attendance.entity.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
	private DateOperation dateOperation = new DateOperation();
	private UserManagerService userManagerService = new UserManagerService();


	/**
	 * 打卡记录分析核心方法，分析一条打卡记录信息<br/>
		<b>迟到</b>：晚于最迟“迟到起算时间”，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>早退</b>：下班打卡时间早于“迟到起算时间”，并且工作当日时间不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>异常</b>：缺少签到退中的1条打卡数据 或者 上下班打卡时间都在最迟起算时间内，不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>缺勤</b>：当天没有打卡数据，并且当天是工作日（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）
	 * @param detail
	 * @param attendanceScheduleSetting
	 * @param dateOperation
	 * @throws Exception 
	 */
	AttendanceDetail analyseCore( AttendanceDetail detail, AttendanceScheduleSetting attendanceScheduleSetting, DateOperation dateOperation, Boolean debugger ) throws Exception {
		if( dateOperation == null ){
			dateOperation = new DateOperation();
		}
		if( detail == null ){
			throw new Exception("detail is null!" );
		}
		//排班规则 早上9：00 - 晚上18:00
		/**
		 * 新规则
		 * 早上9:00 - 中午12:00
		 * 中午13:00 - 晚上18:00
		 */
		if( attendanceScheduleSetting == null ){
			throw new Exception("attendanceScheduleSetting is null, empName:" + detail.getEmpName() );
		}

		Date onDutyTime = null, offDutyTime = null, middleDutyTime = null;
		//上班签到时间
		Date onWorkTime = null, offWorkTime = null, middleWorkStartTime = null, middleWorkEndTime = null;
		Date lateStartTime = null, leaveEarlyStartTime = null, absenceStartTime = null;
		Date morningEndTime = null;

		//考勤异常时段
//		StringBuffer abnormalDutyDayTime = new StringBuffer();
		
		//先初始化当前打卡信息中的上下班时间要求，该要求是是根据员工所在组织排班信息获取到的
		//中午打卡时间要求 13:30
		try {
			logger.debug( debugger, ">>>>>>>>>>格式化[上班签到时间]onWorkTime=" +  detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			onWorkTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnWorkTime() );
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			onWorkTime = null;
			logger.debug( debugger, ">>>>>>>>>>系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime());
			logger.error(e);
		}

		try {
			logger.debug(debugger, ">>>>>>>>>>格式化[中午开始时间]middleStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getMiddayRestStartTime());
			middleWorkStartTime = dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestStartTime());
			logger.debug(debugger, ">>>>>>>>>>格式化[中午结束时间]middleEndTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getMiddayRestEndTime());
			middleWorkEndTime = dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestEndTime());
		} catch (Exception e) {
			detail.setDescription(detail.getDescription() + "; 系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getMiddayRestStartTime() + " - " + attendanceScheduleSetting.getMiddayRestEndTime());
			onWorkTime = null;
			logger.debug(debugger, ">>>>>>>>>>系统进行时间转换时发生异常,onWorkTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getMiddayRestStartTime() + " - " + attendanceScheduleSetting.getMiddayRestEndTime());
			logger.error(e);
		}

		try {
			logger.debug( debugger, ">>>>>>>>>>格式化[下班签退时间]offWorkTime=" +  detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			offWorkTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffWorkTime() );
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			offWorkTime = null;
			logger.debug( debugger, ">>>>>>>>>>系统进行时间转换时发生异常,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			logger.error(e);
		}
		
		if( StringUtils.isNotEmpty( attendanceScheduleSetting.getLateStartTime() ) ){
			try {
				lateStartTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + attendanceScheduleSetting.getLateStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,lateStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLateStartTime() );
				lateStartTime = null;
				logger.warn( "系统进行时间转换时发生异常,lateStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLateStartTime());
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>迟到时间设置为空！系统将不判断迟到情况");
		}
		
		if( StringUtils.isNotEmpty( attendanceScheduleSetting.getLeaveEarlyStartTime() ) ){
			try {
				logger.debug( debugger, ">>>>>>>>>>格式化[早退起算时间]leaveEarlyStartTime=" +  detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
				leaveEarlyStartTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
				leaveEarlyStartTime = null;
				logger.warn( "系统进行时间转换时发生异常,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getLeaveEarlyStartTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>早退时间设置为空！系统将不判断早退情况");
		}
		
		if( StringUtils.isNotEmpty( attendanceScheduleSetting.getAbsenceStartTime() ) ){
			try {
				logger.debug( debugger, ">>>>>>>>>>格式化[缺勤起算时间]absenceStartTime=" +  detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
				absenceStartTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,absenceStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
				absenceStartTime = null;
				logger.warn( "系统进行时间转换时发生异常,absenceStartTime=" + detail.getRecordDateString() + " " + attendanceScheduleSetting.getAbsenceStartTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>上午缺勤时间设置为空！系统将不判上午缺勤情况");
		}
		
		try {
			//上午工作结束时间取中午打卡开始时间

			logger.debug(debugger, ">>>>>>>>>>格式化[上午工作结束时间]morningEndTime=" + attendanceScheduleSetting.getMiddayRestStartTime());
			morningEndTime = dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestStartTime());
		} catch (Exception e) {
			detail.setDescription(detail.getDescription() + "; 系统进行时间转换时发生异常,morningEndTime=" + detail.getRecordDateString() + attendanceScheduleSetting.getMiddayRestStartTime());
			morningEndTime = null;
			logger.warn("系统进行时间转换时发生异常,morningEndTime=" + detail.getRecordDateString() + attendanceScheduleSetting.getMiddayRestStartTime());
			logger.error(e);
		}

		//规则：上午打卡时间、下班打卡时间、中午开始时间、中午结束时间
		if (onWorkTime != null && offWorkTime != null && middleWorkStartTime != null && middleWorkEndTime != null) {
			logger.debug( debugger, ">>>>>>>>>>上下班排班信息获取正常：onWorkTime=" +  onWorkTime + "， offWorkTime="+offWorkTime );
			//获取员工签到时间(签到规则，上午9：00)
			try {
				if( detail.getOnDutyTime() == null || detail.getOnDutyTime().isEmpty() ){
					logger.debug( debugger, ">>>>>>>>>>onDutyTime 为空 " );
					onDutyTime = null;
				}else{
					logger.debug( debugger, ">>>>>>>>>>格式化onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
					onDutyTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnDutyTime() );
				}
			} catch (Exception e) {
				onDutyTime = null;
				logger.warn( "系统进行时间转换时发生异常,onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
				logger.error(e);
			}

			//获取员工中午签到时间---------------------------------------------
			try {
				if ( StringUtils.isEmpty(detail.getMiddayRestStartTime())) {
					logger.debug(debugger, "middayRestStartTime 为空 ");
					middleDutyTime = null;
				} else {
					logger.debug(debugger, "middayRestStartTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime());
					middleDutyTime = dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestStartTime());
				}
			} catch (Exception e) {
				middleDutyTime = null;
				logger.warn("系统进行时间转换时发生异常,middayRestStartTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime());
				logger.error(e);
			}

			//获取员工签退时间（签退规则，下午18:00）
			try {
				if( detail.getOffDutyTime() == null || detail.getOffDutyTime().isEmpty() ){
					logger.debug( debugger, ">>>>>>>>>>offDutyTime 为空" );
					offDutyTime = null;
				}else{
					logger.debug( debugger, ">>>>>>>>>>格式化offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
					offDutyTime = dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffDutyTime() );
				}
			} catch (Exception e) {
				offDutyTime = null;
				logger.warn( "系统进行时间转换时发生异常,offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
				logger.error(e);
			}

			
			//=========================================================================================================
			//=====如果员工没有签到并且没有签退，一条打卡时间都没有，那么是算缺勤的========================================
			//=========================================================================================================
			if (onDutyTime == null && offDutyTime == null && middleDutyTime == null) {
				//if( detail.getIsGetSelfHolidays()  && "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ) ){
				if( detail.getIsGetSelfHolidays()  ){
					logger.debug( debugger, ">>>>>>>>>>请幸运，全天请假不计缺勤。" );
					detail.setAttendance( 0.0 );
					detail.setIsAbsent( false );
					detail.setAbsence( 0.0 );
					detail.setAbsentDayTime("无");
					detail.setWorkTimeDuration( 0L );
				}else{
					if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
						|| detail.getIsHoliday() //或者是节假日
					){
						logger.debug( debugger, ">>>>>>>>>>未请假，不是工作日，不计缺勤。" );
						detail.setAttendance( 0.0 );
						detail.setIsAbsent( false );
						detail.setAbsence( 0.0 );
						detail.setAbsentDayTime("无");
						detail.setWorkTimeDuration( 0L );
					}else{
						logger.debug( debugger, ">>>>>>>>>>未请假，工作日，计缺勤1天。" );
						detail.setAttendance( 0.0 );
						detail.setIsAbsent( true );
						detail.setAbsence( 1.0 );
						detail.setAbsentDayTime("全天");
						detail.setWorkTimeDuration( 0L );
					}
				}
			}else{
				//=========================================================================================================
				//=====上午  如果员工已经签到================================================================================
				//=========================================================================================================
				if( onDutyTime != null ){
					logger.debug( debugger, ">>>>>>>>>>上午打过卡，时间：onDutyTime=" + onDutyTime + ", 上午工作结束时间：morningEndTime=" + morningEndTime );					
					//absenceStartTimes可以不配置，如果不配置，则为null
					//上午签到过了，如果排班设置里已经配置过了旷工起算时间，那么判断员工是否已经缺勤，如果未休假，则视为缺勤半天
					if( absenceStartTime != null && onDutyTime.after( absenceStartTime )){
						logger.debug( debugger, ">>>>>>>>>>上午打卡时间晚于缺勤计时时间......" );
						if( detail.getIsGetSelfHolidays()  && ("上午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
							logger.debug( debugger, ">>>>>>>>>>请幸运，请假不计考勤，出勤只算半天，但请过假了不算缺勤" );
							detail.setIsAbsent( false );
							detail.setAbsence(0.0);
						}else{
							if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
								){
								logger.debug( debugger, ">>>>>>>>>>休息天，不算缺勤，出勤最多只能算半天" );
								detail.setIsAbsent( false );
								detail.setAbsence(0.0);
							}else{
								logger.debug( debugger, ">>>>>>>>>>呵呵，没请假，缺勤半天，出勤最多只能算半天" );
								detail.setIsAbsent( true );
								composeAbsenceStatusForAttendanceDetail(detail);
								detail.setAbsentDayTime("上午");
							}
						}
						//缺勤直接扣半天出勤, 请假不算出勤，所以也只有半天
						composeAttendanceStatusForAttendanceDetail(detail);
					}else if( lateStartTime != null && onDutyTime.after( lateStartTime )){ 
						//上午签到过了，如果排班设置里已经配置过了迟到起算时间，那么判断员工是否已经迟到，如果未休假
						detail.setAttendance( 1.0 );//迟到没关系，出勤时间不用扣半天
						logger.debug( debugger, ">>>>>>>>>>上午打卡时间晚于迟到计时时间......" );
						if( detail.getIsGetSelfHolidays()  && ("上午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))
						){
							logger.debug( debugger, ">>>>>>>>>>请幸运，请过假了不算迟到" );
							detail.setLateTimeDuration( 0L ); //请假了不算迟到
							detail.setIsLate(false );//请假了不算迟到
						}else{
							if( ( detail.getIsWeekend()  && !detail.getIsWorkday()) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
								){
								detail.setLateTimeDuration( 0L );
								detail.setIsLate( false );
								logger.debug( debugger, ">>>>>>>>>>休息天，不算迟到" );
							}else{
								if( onDutyTime == null || offDutyTime == null ){
									detail.setIsAbnormalDuty( true );
								}else{
									long minutes = dateOperation.getMinutes( onWorkTime, onDutyTime ); //迟到计算从上班时间开始计算，不是迟到起算时间
									detail.setLateTimeDuration( minutes );//没请假算迟到时长 
									detail.setIsLate( true );//没请假算迟到
									logger.debug( debugger, ">>>>>>>>>>呵呵，没请假，计迟到一次，迟到时长：minutes=" + minutes );
								}
							}
						}
						long minutes = dateOperation.getMinutes( onDutyTime, morningEndTime );
						logger.debug( debugger, ">>>>>>>>>>上午工作时长, 从"+onDutyTime+"到"+morningEndTime+" ：minutes=" + minutes + "分钟。" );
						detail.setWorkTimeDuration( minutes );//记录上午的工作时长
					}else{//上午正常打卡
						long minutes = dateOperation.getMinutes( onWorkTime, morningEndTime );
						logger.debug( debugger, ">>>>>>>>>>上午工作时长, 从"+onDutyTime+"到"+morningEndTime+" ：minutes=" + minutes + "分钟。" );
						detail.setWorkTimeDuration( minutes );//记录上午的工作时长
					}

				}else{
					logger.debug( debugger, ">>>>>>>>>>员工上午未打卡，异常状态......" );
					if( detail.getIsGetSelfHolidays() && ("上午".equalsIgnoreCase( detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))
					){
						logger.debug( debugger, ">>>>>>>>>>请幸运，请假不计考勤，不需要打卡，不算异常" );
						detail.setIsAbsent( false );
					}else{
						if( ( detail.getIsWeekend()&& !detail.getIsWorkday() ) //周末，并且未调休为工作日
								|| detail.getIsHoliday() //或者是节假日
							){
							logger.debug( debugger, ">>>>>>>>>>休息天，不算打卡异常，本来就不需要打卡" );
							detail.setAbnormalDutyDayTime("无");
							detail.setIsAbnormalDuty( false );
						}else{
							logger.debug(debugger, ">>>>>>>>>>呵呵，没请假，算缺勤。");
							//========================== 是否异常 =================================
//							abnormalDutyDayTime.append("上午");
//							detail.setAbnormalDutyDayTime(abnormalDutyDayTime.toString());
//							detail.setIsAbnormalDuty(true);
							//===================== 记录缺勤 =========================
							detail.setIsAbsent(true);
							composeAbsenceStatusForAttendanceDetail(detail);
							detail.setAbsentDayTime("上午");
							//===================== 记录出勤 =========================
							composeAttendanceStatusForAttendanceDetail(detail);
							//detail.setAttendance( 0.5 ); //上午打卡异常，不知道算不算出勤时间
						}
					}
					logger.debug( debugger, ">>>>>>>>>>上午工作时长, 未打卡：minutes= 0 分钟。" );
					detail.setWorkTimeDuration( 0L );//记录上午的工作时长
				}


				//=========================================================================================================
				//=====中午  如果员工已经签到================================================================================
				//=========================================================================================================
				if(middleDutyTime == null || !middleWorkStartTime.before(middleDutyTime) || !middleWorkEndTime.after(middleDutyTime)){
//					===============  异常  ===============
					/*if(abnormalDutyDayTime.length() > 0){
						detail.setAbnormalDutyDayTime("|中午");
					}else {
						detail.setAbnormalDutyDayTime("中午");
					}

					detail.setIsAbnormalDuty(true);*/
					if (detail.getIsGetSelfHolidays() && ("上午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase(detail.getSelfHolidayDayTime()))) {
						logger.debug(debugger, ">>>>>>>>>>很幸运，请假不计考勤，出勤只算半天，但请过假了不算缺勤");
						detail.setIsAbsent(false);
						detail.setAbsence(0.0);
					} else {
						if ((detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
								|| detail.getIsHoliday() //或者是节假日
						) {
							logger.debug(debugger, ">>>>>>>>>>休息天，不算缺勤，出勤最多只能算半天");
							detail.setIsAbsent(false);
							detail.setAbsence(0.0);
						} else {
							logger.debug(debugger, ">>>>>>>>>>呵呵，没请假，缺勤半天，出勤最多只能算半天");
							//===================== 记录缺勤 =========================
							detail.setIsAbsent(true);
							composeAbsenceStatusForAttendanceDetail(detail);
							detail.setAbsentDayTime("中午");
							//===================== 记录出勤 =========================
							composeAttendanceStatusForAttendanceDetail(detail);
							detail.setAbnormalDutyDayTime("中午");
							detail.setIsAbnormalDuty(true);

						}
					}

				}

				//=========================================================================================================
				//=====下午  如果员工已经签退================================================================================
				//=========================================================================================================
				if( offDutyTime != null ){
					logger.debug( debugger, ">>>>>>>>>>早退计时时间：leaveEarlyStartTime=" + leaveEarlyStartTime );
					long minutes = dateOperation.getMinutes( morningEndTime, offDutyTime);
					logger.debug( debugger, ">>>>>>>>>>下午工作时长, 从"+morningEndTime+"到"+offDutyTime+" ：minutes=" + minutes + "分钟。" );
					detail.setWorkTimeDuration( detail.getWorkTimeDuration() + minutes );//记录上午的工作时长 + 下午工作时长
					logger.debug( debugger, ">>>>>>>>>>全天工作时长, ：minutes=" + detail.getWorkTimeDuration() + "分钟。" );
					if( leaveEarlyStartTime != null && offDutyTime.before( leaveEarlyStartTime )){
						logger.debug( debugger, ">>>>>>>>>>下午打卡时间早于早退计时时间......" );
						if( detail.getIsGetSelfHolidays() && ("下午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
							logger.debug( debugger, ">>>>>>>>>>请幸运，请假不计考勤，出勤只算半天，但请过假了不算早退" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
							//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤时间
						}else{
							if( ( detail.getIsWeekend() && !detail.getIsWorkday() ) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
							){
								logger.debug( debugger, ">>>>>>>>>>休息天，不算早退" );
								detail.setLeaveEarlierTimeDuration( 0L );
								detail.setIsLeaveEarlier( false );
								//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤
							}else{
//								minutes = dateOperation.getMinutes( offDutyTime, leaveEarlyStartTime );
								minutes = dateOperation.getMinutes( offDutyTime, offWorkTime );
								detail.setLeaveEarlierTimeDuration(minutes); //早退时间
//								detail.setIsLeaveEarlier( true );
								//===================== 记录缺勤 =========================
								detail.setIsAbsent(true);
								composeAbsenceStatusForAttendanceDetail(detail);
								detail.setAbsentDayTime("下午");
								//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤
								//=========================== 记录出勤 =========================
								composeAttendanceStatusForAttendanceDetail(detail);
							}
						}
						//在早退起算之后，并在下班时间之前
					} else if ((leaveEarlyStartTime != null && offDutyTime.after(leaveEarlyStartTime)) && offDutyTime.before(offWorkTime)) {
						logger.debug(debugger, ">>>>>>>>>>下午打卡时间晚于早退计时时间早于下班时间......");
						if (detail.getIsGetSelfHolidays() && ("下午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase(detail.getSelfHolidayDayTime()))) {
							logger.debug(debugger, ">>>>>>>>>>请幸运，请假不计考勤，出勤只算半天，但请过假了不算早退");
							detail.setLeaveEarlierTimeDuration(0L);
							detail.setIsLeaveEarlier(false);
							//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤时间
						} else {
							if ((detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
							) {
								logger.debug(debugger, ">>>>>>>>>>休息天，不算早退");
								detail.setLeaveEarlierTimeDuration(0L);
								detail.setIsLeaveEarlier(false);
								//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤
							} else {
//								minutes = dateOperation.getMinutes(offDutyTime, leaveEarlyStartTime);
								minutes = dateOperation.getMinutes(offDutyTime, offWorkTime);
								detail.setLeaveEarlierTimeDuration(minutes); //早退时间
								detail.setIsLeaveEarlier(true);

							}
						}
					}
				}else{
					//如果打卡是今天，但是还没有到下班打卡时间
					if(  detail.getRecordDateString().equals( dateOperation.getNowDate() )  && dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffWorkTime()).after( new Date() ) ) {
						detail.setAbnormalDutyDayTime("无");
						detail.setIsAbnormalDuty(false);
						logger.debug( debugger, ">>>>>>>>>>还没有到下班打卡时间，先不分析结果。" );
					}else {
						logger.debug( debugger, ">>>>>>>>>>员工下午未打卡，属于异常状态......" );
						//员工未签退，算缺勤了半天，出勤率: - 0.5
						if( detail.getIsGetSelfHolidays()  && ("下午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
							logger.debug( debugger, ">>>>>>>>>>请幸运，请假不计考勤，不需要打卡，不算异常状态" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
							composeAttendanceStatusForAttendanceDetail(detail);
							//detail.setAttendance( detail.getAttendance() - 0.5 );
						}else{
							if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
									|| detail.getIsHoliday() //或者是节假日
								){
								logger.debug( debugger, ">>>>>>>>>>休息天，不算异常" );
								detail.setAbnormalDutyDayTime("无");
								detail.setIsAbnormalDuty(false);
							}else{
//								if(abnormalDutyDayTime.length() > 0){
//									detail.setAbnormalDutyDayTime("|下午");
//								}else {
//									detail.setAbnormalDutyDayTime("下午");
//								}
								//===================== 记录缺勤 =========================
								detail.setIsAbsent(true);
								composeAbsenceStatusForAttendanceDetail(detail);
								detail.setAbsentDayTime("下午");
								//=========================== 记录出勤 =========================
								composeAttendanceStatusForAttendanceDetail(detail);
								detail.setIsAbnormalDuty(true);
								logger.debug( debugger, ">>>>>>>>>>呵呵，没请假，未打卡，算异常状态。" );
							}
						}						
					}
					logger.debug( debugger, ">>>>>>>>>>全天工作时长,[下午未打卡，只算上午的工作时长] ：minutes=" + detail.getWorkTimeDuration() + "分钟。" );
				}
			}
			
			
			//=========================================================================================================
			//=====如果上下班都打卡了，但是工作时间没有9小时，那么属于工时不足(如果迟到了，就算迟到，不算工时不足)========================================
			//=========================================================================================================
			if( onDutyTime != null && offDutyTime != null){
				if( ( detail.getIsWeekend() && !detail.getIsWorkday() ) //周末，并且未调休为工作日
						|| detail.getIsHoliday() //或者是节假日
					){
					//没事
				}else{
					if( !detail.getIsGetSelfHolidays() ){ // 没请假
						//计算工时
						long minutes = dateOperation.getMinutes( onDutyTime, offDutyTime);
						if( minutes < (60*9)){
							logger.debug( debugger, ">>>>>>>>>>签到时间["+onDutyTime+"]，签退时间["+offDutyTime+"]，工作时间:"+minutes+"分钟！工时不足！" );
							if( !detail.getIsLate() ){
								detail.setIsLackOfTime(true);//工时不足
							}
						}
					}
				}
			}
			
			detail.setRecordStatus( 1 );
		}
		return detail;
	}

	/**
	 * 分析打卡信息，为打卡数据计算缺勤天数信息
	 * @param detail
	 */
	private void composeAbsenceStatusForAttendanceDetail(AttendanceDetail detail) {
		Double absence = detail.getAbsence();
		if (absence < 1.0) {
			detail.setAbsence(absence + 0.5);
		}
	}

	//

	/**
	 * 分析打卡信息，为打卡数据计算出勤天数信息
	 * @param detail
	 */
	private void composeAttendanceStatusForAttendanceDetail(AttendanceDetail detail) {
		Double attendance = detail.getAttendance();
		if (attendance > 0.0) {
			detail.setAttendance(attendance - 0.5);
		}
	}

}

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
 * 2、一天三次打卡：打上班，下班两次卡外，中午休息时间也需要打一次卡，以确保员工在公司活动
 *
 */
class AttendanceDetailAnalyseSignProxy3 {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseSignProxy3.class );
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 3、一天四次打卡：打上午上班，上午下班，下午上班，下午下班四次卡
	 * @param detail
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 */
	public AttendanceDetail analyse( AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger ) throws Exception {
		Date onWorkTime = null, offWorkTime = null;
		Date onDutyTime = null, offDutyTime = null, morningOffdutyTime = null, afternoonOndutyTime = null;
		Date lateStartTime = null, leaveEarlyStartTime = null, absenceStartTime = null, morningEndTime = null, afternoonStartTime = null;

		//周末，并且未调休为工作日或者是节假日
		Boolean isNotWorkDay = ( detail.getIsWeekend() && !detail.getIsWorkday()) || detail.getIsHoliday();

		//员工请假状态
		Boolean isSelfHoliday_FullDay = detail.getIsGetSelfHolidays() && "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() );
		Boolean isSelfHoliday_Morning = detail.getIsGetSelfHolidays() && "上午".equalsIgnoreCase( detail.getSelfHolidayDayTime() );
		Boolean isSelfHoliday_Afternoon = detail.getIsGetSelfHolidays() && "下午".equalsIgnoreCase( detail.getSelfHolidayDayTime() );
		Boolean isSelfHoliday = isSelfHoliday_Afternoon || isSelfHoliday_Morning || isSelfHoliday_FullDay;

		//先初始化当前打卡信息中的上下班时间要求，该要求是是根据员工所在组织排班信息获取到的
		onWorkTime = AttendanceDetailAnalyseCoreService.getOnWorkTimeFromDetail( detail, debugger );
		morningEndTime = AttendanceDetailAnalyseCoreService.getMiddleRestStartTimeFromDetail( detail, scheduleSetting, debugger );
		afternoonStartTime = AttendanceDetailAnalyseCoreService.getMiddleRestEndTimeFromDetail( detail, scheduleSetting, debugger );
		offWorkTime = AttendanceDetailAnalyseCoreService.getOffWorkTimeFromDetail( detail, debugger );
		
		onDutyTime = AttendanceDetailAnalyseCoreService.getOnDutyTimeFromDetail( detail, debugger );
		offDutyTime = AttendanceDetailAnalyseCoreService.getOffDutyTimeFromDetail( detail, debugger );
		morningOffdutyTime = AttendanceDetailAnalyseCoreService.getMorningOffdutyTimeFromDetail( detail, debugger );
		afternoonOndutyTime = AttendanceDetailAnalyseCoreService.getAfternoonOndutyTimeFromDetail( detail, debugger );

		lateStartTime = AttendanceDetailAnalyseCoreService.getLateStartTimeFromDetail( detail, scheduleSetting, debugger );
		leaveEarlyStartTime = AttendanceDetailAnalyseCoreService.getLeaveEarlyStartTimeFromDetail( detail, scheduleSetting, debugger );
		absenceStartTime = AttendanceDetailAnalyseCoreService.getAbsenceStartTimeFromDetail( detail, scheduleSetting, debugger );

		if ( onWorkTime != null && offWorkTime != null && morningEndTime != null && afternoonStartTime != null ) {
			logger.debug( debugger, "上下班排班信息获取正常：onWorkTime=" +  onWorkTime + "， morningEndTime="+morningEndTime + "， afternoonStartTime="+afternoonStartTime + "， offWorkTime="+offWorkTime );
			logger.debug( debugger, "上下班签到信息获取正常：onDutyTime=" +  onDutyTime + "， morningOffdutyTime="+morningOffdutyTime + "， afternoonOndutyTime="+afternoonOndutyTime + "， offDutyTime="+offDutyTime );

			//规则：如果员工没有签到并且没有签退，一条打卡时间都没有，那么可能会是算缺勤的
			if ( onDutyTime == null && offDutyTime == null ) {
				//如果员工已经全天请假了，则不算缺勤，考勤结果正常
				if( isSelfHoliday_FullDay || isNotWorkDay ){
					logger.debug( debugger, "全天请假不计缺勤。" );
					detail.setAttendance( 0.0 );
					detail.setIsAbsent( false );
					detail.setAbsence( 0.0 );
					detail.setAbsentDayTime( "无" );
					detail.setWorkTimeDuration( 0L );
				}else{
					//否则，没有打卡，又不是请假也不是周末和节假日，那么需要计为缺勤（如果员工没有签到并且没有签退，一条打卡时间都没有）
					logger.debug( debugger, "未请假，工作日，计缺勤1天。" );
					detail.setAttendance( 0.0 );
					detail.setIsAbsent( true );
					detail.setAbsence( 1.0 );
					detail.setAbsentDayTime("全天");
					detail.setWorkTimeDuration( 0L );
				}
			}else{
				detail.setAttendance( 1.0 );//默认为全天出勤了
				detail.setIsAbsent( false );//默认为未缺勤
				detail.setAbsence( 0.0 );//默认为未缺勤

				//=========================================================================================================
				//=====上午  如果员工已经签到================================================================================
				//=========================================================================================================
				if( onDutyTime != null ){
					logger.debug( debugger, "上午打过卡，时间：onDutyTime=" + onDutyTime );
					//缺勤起算时间：absenceStartTimes可以不配置，如果不配置，则为null，则不会在为打卡时间过晚而导致缺勤
					//上午签到过了，如果排班设置里已经配置过了缺勤起算时间，那么判断员工是否已经缺勤，如果未休假，则视为缺勤半天
					if( absenceStartTime != null && onDutyTime.after( absenceStartTime )){
						logger.debug( debugger, "上午打卡时间晚于缺勤计时时间：" + absenceStartTime );
						//判断员工是否已经请假过了
						if(  isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "请假不计出勤，但请过假了不算缺勤" );
							detail.setIsAbsent( false );
							detail.setAbsence(0.0);
						}else{
							logger.debug( debugger, "没请假，缺勤半天" );
							detail.setAbsentDayTime("上午");
							detail.setIsAbsent( true );
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);//递增缺勤天数 + 0.5
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//递减出勤天数 - 0.5
					}else if( lateStartTime != null && onDutyTime.after( lateStartTime )){
						//上午签到过了，并没有超过缺勤起算时间，如果排班设置里已经配置过了迟到起算时间，那么判断员工是否已经迟到，如果未休假也不是周末的话
						logger.debug( debugger, "上午打卡时间晚于迟到计时时间......" );
						if( isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "请过假了不算迟到" );
							detail.setLateTimeDuration( 0L ); //请假了不算迟到
							detail.setIsLate( false );//请假了不算迟到
						}else{
							//迟到计算从上班时间开始计算，不是迟到起算时间
							long minutes = dateOperation.getMinutes( onWorkTime, onDutyTime );
							detail.setLateTimeDuration( minutes );//没请假算迟到时长
							detail.setIsLate( true );//没请假算迟到
							logger.debug( debugger, "计迟到一次，迟到时长：minutes=" + minutes );
						}

					}
				}else{
					logger.debug( debugger, "员工上午缺卡" );
					if(  isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
						logger.debug( debugger, "请假不计考勤，不需要打卡，不算异常" );
						detail.setIsAbsent( false );
						detail.setAbnormalDutyDayTime("无");
						detail.setIsAbnormalDuty( false );
					}else{
						logger.debug(debugger, "没打卡，没请假，工作日，算缺勤。");
						detail.setAbnormalDutyDayTime("上午");
						detail.setIsAbnormalDuty(true);
						detail.setAbsentDayTime("上午");
						detail.setIsAbsent(true);
						AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
					}
					AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//递减出勤天数 - 0.5
					logger.debug( debugger, "上午工作时长, 未打卡：minutes= 0 分钟。" );
					detail.setWorkTimeDuration( 0L );
				}

				//=========================================================================================================
				//=====上午  如果员工已经签退 morningOffdutyTime================================================================================
				//=========================================================================================================
				if( morningOffdutyTime != null ){//打卡签退，上午不判断是否早退
					long minutes = 0L;
					//上午已经签退了，现在计算上午的工作时长
					if (onDutyTime != null ) {
						minutes = dateOperation.getMinutes(onDutyTime, morningOffdutyTime);
					}
					detail.setWorkTimeDuration( minutes );
					logger.debug( debugger, "直接计算上午工作时长, 从"+ onDutyTime +"到" + morningOffdutyTime + " ：minutes=" + minutes + "分钟。" );
				}else{
					//看看时间是否已经到了下午上班时间
					if( new Date().before(afternoonStartTime )){
						logger.debug( debugger, "上午还未结束，先不分析结果。" );
					}else{
						logger.debug( debugger, "员工上午未签退，属于异常状态，缺卡" );
						if(   isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "请假或者非工作日不计考勤，不需要打卡，不算异常状态" );
//							detail.setLeaveEarlierTimeDuration( 0L );
//							detail.setIsLeaveEarlier( false );
						}else{
							detail.setAbnormalDutyDayTime("上午");
							detail.setAbsentDayTime("上午");
							detail.setIsAbnormalDuty(true);
							detail.setIsAbsent(true);
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
							logger.debug( debugger, "没请假，未打卡，算缺卡和缺勤状态。" );
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//递减出勤天数 - 0.5
						//员工未签退，计算全天的工作时长，下午的时长不计算
						detail.setWorkTimeDuration( 0L );
						logger.debug( debugger, "员工未签退，上午工作时长暂时记为0分钟，等待补卡或者补提请假申请。" );
					}
				}

				//=========================================================================================================
				//=====下午  如果员工已经签退 afternoonOndutyTime================================================================================
				//=========================================================================================================
				if( afternoonOndutyTime != null ){
					logger.debug( debugger, "下午打卡签到，时间：afternoonOndutyTime=" + afternoonOndutyTime );
				}else{
					if( new Date().after( afternoonStartTime )){
						logger.debug( debugger, "员工下午上班缺卡" );
						if(  isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "请假不计考勤，不需要打卡，不算异常" );
//							detail.setIsAbsent( false );
//							detail.setAbnormalDutyDayTime("无");
//							detail.setIsAbnormalDuty( false );
						}else{
							logger.debug(debugger, "没打卡，没请假，工作日，算缺勤。");
							detail.setAbnormalDutyDayTime("下午");
							detail.setIsAbnormalDuty(true);
							detail.setAbsentDayTime("下午");
							detail.setIsAbsent(true);
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//递减出勤天数 - 0.5
						logger.debug( debugger, "下午工作时长, 未打卡：minutes= 0 分钟。" );
//						detail.setWorkTimeDuration( 0L );
					}
				}

				//=========================================================================================================
				//=====下午  如果员工已经签退================================================================================
				//=========================================================================================================
				if( offDutyTime != null ){
					long minutes = 0L;
					logger.debug( debugger, "早退计时时间：leaveEarlyStartTime=" + leaveEarlyStartTime );
					if( leaveEarlyStartTime != null && offDutyTime.before( leaveEarlyStartTime )){
						logger.debug( debugger, "下午打卡时间早于早退计时时间" );
						if(  isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "请假、休息天不计考勤，不算出勤，不算早退" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
						}else{
							minutes = dateOperation.getMinutes( offDutyTime, offWorkTime );//计算早退时长
							detail.setLeaveEarlierTimeDuration(minutes); //早退时长
							detail.setIsLeaveEarlier( true );
						}
					}
//					else if ((leaveEarlyStartTime != null && offDutyTime.after(leaveEarlyStartTime)) && offDutyTime.before(offWorkTime)) {
//						//打卡在早退起算之后，并在下班时间之前
//						logger.debug(debugger, "下午打卡时间晚于早退计时时间早于下班时间......");
//						if ( isSelfHoliday_Afternoon || isSelfHoliday_Afternoon || isNotWorkDay ) {
//							logger.debug(debugger, "请假不计考勤，出勤只算半天，但请过假了不算早退");
//							detail.setLeaveEarlierTimeDuration(0L);
//							detail.setIsLeaveEarlier(false);
//						} else {
//							minutes = dateOperation.getMinutes(offDutyTime, offWorkTime);//计算早退时长
//							detail.setLeaveEarlierTimeDuration(minutes); //早退时长
//							detail.setIsLeaveEarlier(true);
//						}
//					}
					//下午已经签退了，现在计算全天的工作时长
					minutes = dateOperation.getMinutes( afternoonStartTime, offDutyTime);
					logger.debug( debugger, "计算下午工作时长, 从"+ afternoonStartTime +"到" + offDutyTime + " ：minutes=" + minutes + "分钟。" );
					logger.debug( debugger, "直接计算全天工作时长, "+ detail.getWorkTimeDuration() +"+" + minutes + "=" + detail.getWorkTimeDuration() + minutes + "分钟。" );
					detail.setWorkTimeDuration( detail.getWorkTimeDuration() + minutes );//记录上午的工作时长 + 下午工作时长
				}else{
					//员工未签退：有两种情况，还没有到打卡时间，或者说没有打卡
					//如果打卡是今天，但是今天还没有结束
					if( detail.getRecordDateString().equals( dateOperation.getNowDate() )) {
						detail.setAbnormalDutyDayTime("无");
						detail.setIsAbnormalDuty(false);
						logger.debug( debugger, "今天还未结束，先不分析结果。" );
					}else {
						logger.debug( debugger, "员工下午未打卡，属于异常状态，缺卡" );
						if(  isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "请假或者非工作日不计考勤，不需要打卡，不算异常状态" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
						}else{
							if( StringUtils.equals( "上午", detail.getAbnormalDutyDayTime()) ){
								detail.setAbnormalDutyDayTime("上午|下午");
							}else {
								detail.setAbnormalDutyDayTime("下午");
							}
							if( StringUtils.equals( "上午", detail.getAbsentDayTime()) ){
								detail.setAbsentDayTime("全天");
							}else{
								detail.setAbsentDayTime("下午");
							}
							detail.setIsAbnormalDuty(true);
							detail.setIsAbsent(true);
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
							logger.debug( debugger, "没请假，未打卡，算缺卡和缺勤状态。" );
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//递减出勤天数 - 0.5
						//员工未签退，计算全天的工作时长，下午的时长不计算
//						detail.setWorkTimeDuration( 0L );
						logger.debug( debugger, "员工未签退，全天工作时长暂时记为0分钟，等待补卡或者补提请假申请。" );
					}
				}
			}
			detail.setRecordStatus( 1 );
		}else{
			logger.warn( "上下班排班信未正确配置，无法正常分析考勤记录：onWorkTime=" +  onWorkTime + "， morningEndTime="+morningEndTime + "， afternoonStartTime=" +  afternoonStartTime + "， offWorkTime="+offWorkTime );
		}
		return detail;
	}
}

package com.x.attendance.assemble.control.service;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;


import java.util.Date;
import java.util.List;

/**
 * 考勤打卡记录分析服务类
 * 2、一天三次打卡：打上班，下班两次卡外，中午休息时间也需要打一次卡，以确保员工在公司活动
 */
class ComposeDetailWithMobileInSignProxy2 {
	
	private static  Logger logger = LoggerFactory.getLogger( ComposeDetailWithMobileInSignProxy2.class );
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 组织一个完整的打卡记录，有午休时间记录
	 * 1、上班打卡时间：最早的一次打卡就是上班打卡时间
	 * 2、中午打卡时间，计算午休开始和结束时间内，最晚的那一次打卡时间作为午休打卡时间
	 * 3、下班打卡时间：取最晚的一次打卡
	 * @param mobileDetails
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceDetail compose(List<AttendanceDetailMobile> mobileDetails, AttendanceScheduleSetting scheduleSetting, Boolean debugger) throws Exception {
		AttendanceDetailMobile mobileDetail = mobileDetails.get( 0 );
		String onDutyTime = getOnDutyTime( mobileDetails );
		String offDutyTime = getOffDutyTime( mobileDetails );

		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestStartTime() )){
			scheduleSetting.setMiddayRestStartTime("11:30");
		}

		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestEndTime() )){
			scheduleSetting.setMiddayRestStartTime("13:30");
		}

		String afternonnOnDutyTime =  getAfternoonOnDutyTime( mobileDetails, scheduleSetting.getMiddayRestStartTime(), scheduleSetting.getMiddayRestEndTime() );

		//组织员工当天的考勤打卡记录
		AttendanceDetail detail = new AttendanceDetail();
		detail.setEmpNo( mobileDetail.getEmpNo() );
		detail.setEmpName( mobileDetail.getEmpName() );
		if( mobileDetail.getRecordDate() != null ) {
			detail.setYearString( dateOperation.getYear( mobileDetail.getRecordDate() ) );
			detail.setMonthString( dateOperation.getMonth( mobileDetail.getRecordDate() ) );
		}
		detail.setRecordDateString( mobileDetail.getRecordDateString() );
		detail.setOnDutyTime( onDutyTime ); //最早的一次打卡作为当天的上班签到打卡
		if( StringUtils.equals( afternonnOnDutyTime, onDutyTime )){
			detail.setAfternoonOnDutyTime( null );
		}else{
			detail.setAfternoonOnDutyTime( afternonnOnDutyTime ); //午休打卡时间
		}
		if( StringUtils.equals(offDutyTime, onDutyTime ) || StringUtils.equals( afternonnOnDutyTime, offDutyTime ) ){
			detail.setOffDutyTime( null );
		}else{
			detail.setOffDutyTime( offDutyTime ); //最晚的一次打卡作为当天的下班签退打卡
		}
		detail.setRecordStatus( 0 );
		detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
		return detail;
	}

	/**
	 * 计算午休开始和结束时间内，最晚的那一次打卡时间作为午休打卡时间
	 * @param mobileDetails
	 * @param middayRestStartTime
	 * @param middayRestEndTime
	 * @return
	 */
	private String getAfternoonOnDutyTime(List<AttendanceDetailMobile> mobileDetails, String middayRestStartTime, String middayRestEndTime) throws Exception {
		Date moningOndutyTime = null;
		Date signTime = null, restStartTime=null, restEndTime = null;
		String resultTime = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString( detailMobile.getSignTime() );
				restStartTime = dateOperation.getDateFromString( middayRestStartTime );
				restEndTime = dateOperation.getDateFromString( middayRestEndTime );
				if( moningOndutyTime != null && signTime != null && restStartTime.before( signTime ) && restEndTime.after( signTime )) {
					if( moningOndutyTime.before( signTime ) ){
						moningOndutyTime = signTime;
						resultTime = detailMobile.getSignTime();
					}
				}else if( moningOndutyTime == null ){
					moningOndutyTime = signTime;
					resultTime = detailMobile.getSignTime();
				}
			}
		}
		return resultTime;
	}

	/**
	 * 将最晚的一次打卡时间设置为下班打卡时间
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOffDutyTime( List<AttendanceDetailMobile> mobileDetails ) throws Exception {
		Date offDutyTime = null;
		Date signTime = null;
		String offDutyTimeString = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
				if( offDutyTime != null && signTime != null && offDutyTime.before( signTime )) {
					offDutyTime = signTime;
					offDutyTimeString = detailMobile.getSignTime();
				}else if( offDutyTime == null ){
					offDutyTime = signTime;
					offDutyTimeString = detailMobile.getSignTime();
				}
			}
		}
		return offDutyTimeString;
	}

	/**
	 * 将第一次打卡时间，设置为上班打卡时间
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOnDutyTime(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		Date onDutyTime = null;
		Date signTime = null;
		String onDutyTimeString = null;
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			if( onDutyTime != null && signTime != null && onDutyTime.after( signTime )) {
				onDutyTime = signTime;
				onDutyTimeString = detailMobile.getSignTime();
			}else if( onDutyTime == null ){
				onDutyTime = signTime;
				onDutyTimeString = detailMobile.getSignTime();
			}
		}
		return onDutyTimeString;
	}
}

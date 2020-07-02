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
 * 3、一天四次打卡：打上午上班，上午下班，下午上班，下午下班四次卡
 */
class ComposeDetailWithMobileInSignProxy3 {
	
	private static  Logger logger = LoggerFactory.getLogger( ComposeDetailWithMobileInSignProxy3.class );
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 组织一个完整的打卡记录，有午休时间记录
	 * 1、上午上班打卡时间：上午上班时段中，最早的一次打卡时间就是上午上班打卡时间
	 * 2、上午下班打卡时间：上午上班时段中，最晚的一次打卡时间就是上午下班打卡时间
	 * 3、下午上班打卡时间：下午上班时段中，最早的一次打卡时间就是下午上班打卡时间
	 * 3、下午下班打卡时间：下午上班时段中，最晚的一次打卡时间就是下午下班打卡时间
	 * @param mobileDetails
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceDetail compose(List<AttendanceDetailMobile> mobileDetails, AttendanceScheduleSetting scheduleSetting, Boolean debugger) throws Exception {
		AttendanceDetailMobile mobileDetail = mobileDetails.get( 0 );
		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestStartTime() )){
			scheduleSetting.setMiddayRestStartTime("11:30");
		}
		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestEndTime() )){
			scheduleSetting.setMiddayRestStartTime("13:30");
		}
		String middayRestStartTime = scheduleSetting.getMiddayRestStartTime();
		String middayRestEndTime = scheduleSetting.getMiddayRestEndTime();
		String onWorkTimeStr = scheduleSetting.getOnDutyTime();
		String offWorkTimeStr = scheduleSetting.getOffDutyTime();
		String onDutyTime = getOnDutyTime( mobileDetails, middayRestStartTime );
		String offDutyTime = getOffDutyTime( mobileDetails, middayRestEndTime );
		String morningOffdutyTime = getMorningOffDutyTime(mobileDetails, onWorkTimeStr, middayRestEndTime);
		String afternoonOndutyTime = getAfternoonOnDutyTime( mobileDetails, middayRestStartTime, offWorkTimeStr );

		//组织员工当天的考勤打卡记录
		AttendanceDetail detail = new AttendanceDetail();
		detail.setEmpNo( mobileDetail.getEmpNo() );
		detail.setEmpName( mobileDetail.getEmpName() );
		if( mobileDetail.getRecordDate() != null ) {
			detail.setYearString( dateOperation.getYear( mobileDetail.getRecordDate() ) );
			detail.setMonthString( dateOperation.getMonth( mobileDetail.getRecordDate() ) );
		}
		detail.setRecordDateString( mobileDetail.getRecordDateString() );
		detail.setOnDutyTime( onDutyTime ); //上午上班签到打卡
		detail.setMorningOffDutyTime( morningOffdutyTime ); //上午下班打卡
		detail.setAfternoonOnDutyTime( afternoonOndutyTime ); //下午上班打卡
		detail.setOffDutyTime( offDutyTime ); //下午下班签退打卡
		detail.setRecordStatus( 0 );
		detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
		return detail;
	}

	/**
	 * 计算上午下班打卡时间：离上午上班时间最近的一次打卡时间
	 * 取上午上班之后，到下午上班之前最晚的打卡记录
	 *
	 * @param mobileDetails
	 * @param onWorkTimeStr 上午上班时间
	 * @param middayRestEndTime 下午上班时间
	 * @return
	 */
	private String getMorningOffDutyTime(List<AttendanceDetailMobile> mobileDetails, String onWorkTimeStr, String middayRestEndTime) throws Exception {
		Date moningOffdutyTime = null;
		Date signTime = null, afternoonOnDutyTime = null, onWorkTime = null;
		String result = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString( detailMobile.getSignTime() );
				afternoonOnDutyTime = dateOperation.getDateFromString( middayRestEndTime );
				onWorkTime = dateOperation.getDateFromString( onWorkTimeStr );

				//去掉上午上班前和下午上班后的打卡信息
				if( signTime.before( onWorkTime ) || signTime.after( afternoonOnDutyTime )){
					continue;
				}
				if( moningOffdutyTime != null && signTime != null && moningOffdutyTime.before( signTime ) ) {
					moningOffdutyTime = signTime;
					result = detailMobile.getSignTime();
				}else if( moningOffdutyTime == null ){
					moningOffdutyTime = signTime;
					result = detailMobile.getSignTime();
				}
			}
		}
		return result;
	}

	/**
	 * 计算下午上班打卡：离下午上班时间最近的一次打卡时间
	 * 取上午下班之后，下午下班之前最早的一次打卡时间
	 * @param mobileDetails
	 * @param middayRestStartTime
	 * @param offWorkTimeStr
	 * @return
	 */
	private String getAfternoonOnDutyTime(List<AttendanceDetailMobile> mobileDetails, String middayRestStartTime, String offWorkTimeStr ) throws Exception {
		Date afternoonOnDutyTime = null;
		Date signTime = null, morningOffWorkTime=null, offWorkTime = null;
		String result = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString( detailMobile.getSignTime() );
				morningOffWorkTime = dateOperation.getDateFromString( middayRestStartTime );
				offWorkTime = dateOperation.getDateFromString( offWorkTimeStr );

				//上午下班之前的打卡和下午下班之后的打卡不算，取上午下班后到下午下班之前最量的一次打卡
				if( signTime.before(morningOffWorkTime) || signTime.after( offWorkTime )){
					continue;
				}
				if( afternoonOnDutyTime != null && signTime != null && signTime.before( afternoonOnDutyTime )) {
					afternoonOnDutyTime = signTime;
					result = detailMobile.getSignTime();
				}else if( afternoonOnDutyTime == null ){
					afternoonOnDutyTime = signTime;
					result = detailMobile.getSignTime();
				}
			}
		}
		return result;
	}

	/**
	 * 将下午上班之后所有的打卡中最晚的一次作为当天下午下班的签退打卡
	 *
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOffDutyTime( List<AttendanceDetailMobile> mobileDetails, String middayRestEndTime ) throws Exception {
		Date offDutyTime = null, signTime = null, afterOndutyTime=null;
		String result = null;

		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			afterOndutyTime = dateOperation.getDateFromString( middayRestEndTime );
			//下午上班前的打卡就不算到下午下班打卡了
			if( signTime.before( afterOndutyTime )){
				continue;
			}
			if( offDutyTime != null && signTime != null && offDutyTime.before( signTime )) {
				offDutyTime = signTime;
				result = detailMobile.getSignTime();
			}else if( offDutyTime == null ){
				offDutyTime = signTime;
				result = detailMobile.getSignTime();
			}
		}
		return result;
	}

	/**
	 * 将第一次打卡时间，设置为上班打卡时间
	 * 只可能在第一区间和第二区间，在上午下班时间之前打卡才能算当天上午的上班打卡
	 * @param mobileDetails
	 * @param middayRestStartTime
	 * @return
	 * @throws Exception
	 */
	private String getOnDutyTime(List<AttendanceDetailMobile> mobileDetails, String middayRestStartTime ) throws Exception {
		Date onDutyTime = null, signTime = null, morningOffdutyTime=null;
		String result = null;

		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			morningOffdutyTime = dateOperation.getDateFromString( middayRestStartTime );
			//排除在上午下班时间之后的打卡
			if( signTime.after( morningOffdutyTime )){
				continue;
			}
			if( onDutyTime != null && signTime != null && onDutyTime.after( signTime )) {
				onDutyTime = signTime;
				result = detailMobile.getSignTime();
			}else if( onDutyTime == null ){
				onDutyTime = signTime;
				result = detailMobile.getSignTime();
			}
		}
		return result;
	}
}

package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 考勤打卡记录分析服务类
 * 1、一天只打上下班两次卡
 */
class ComposeDetailWithMobileInSignProxy1 {
	
	private static  Logger logger = LoggerFactory.getLogger( ComposeDetailWithMobileInSignProxy1.class );
	private DateOperation dateOperation = new DateOperation();


	/**
	 * 组织一个完整的打卡记录
	 * 1、上班打卡时间：最早的一次打卡就是上班打卡时间（考虑一下有没有配置中午休息时间，如果有午休时间，上班打卡需要按时间来计算）
	 * 2、下班打卡时间：第二次以后的打卡都是下班打卡，取最晚的一次
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
		String recordAddress = this.getRecordAddress(mobileDetails);
		String optMachineType = this.getOptMachineType(mobileDetails);

		//组织员工当天的考勤打卡记录
		AttendanceDetail detail = new AttendanceDetail();
		detail.setEmpNo( mobileDetail.getEmpNo() );
		detail.setEmpName( mobileDetail.getEmpName() );
		detail.setRecordAddress(recordAddress);
		detail.setOptMachineType(optMachineType);
		if( mobileDetail.getRecordDate() != null ) {
			detail.setYearString( dateOperation.getYear( mobileDetail.getRecordDate() ) );
			detail.setMonthString( dateOperation.getMonth( mobileDetail.getRecordDate() ) );
		}
		detail.setRecordDateString( mobileDetail.getRecordDateString() );
		detail.setOnDutyTime( onDutyTime );
		if( StringUtils.equals(offDutyTime, onDutyTime ) ){
			detail.setOffDutyTime( null );
		}else{
			detail.setOffDutyTime( offDutyTime ); //最晚的一次打卡作为当天的下班签退打卡
		}
		detail.setRecordStatus( 0 );
		detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
		return detail;
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

	/**
	 * 获取打卡地址
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getRecordAddress(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		String mobileAddress = "";
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			String recordAddress = detailMobile.getRecordAddress();
			String workAddress = detailMobile.getWorkAddress();
			String oneAddress = "";
			if(StringUtils.isNotEmpty(workAddress)){
				oneAddress = workAddress;
			}else{
				oneAddress = recordAddress;
			}
			if(StringUtils.isEmpty(mobileAddress)){
				mobileAddress = oneAddress;
			}else{
				if(StringUtils.isNotEmpty(oneAddress) && !StringUtils.contains(mobileAddress,oneAddress)){
					mobileAddress = mobileAddress+","+oneAddress;
				}
			}
		}
		return mobileAddress;
	}

	/**
	 * 获取设备信息
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOptMachineType(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		String optMachineType = "";
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			String tmpOptMachineType = detailMobile.getOptMachineType();
			if(StringUtils.isNotEmpty(tmpOptMachineType)){
				if(StringUtils.isEmpty(optMachineType)){
					optMachineType = tmpOptMachineType;
				}else{
					if(!StringUtils.contains(optMachineType,tmpOptMachineType)){
						optMachineType = optMachineType+","+tmpOptMachineType;
					}
				}
			}
		}
		return optMachineType;
	}
}

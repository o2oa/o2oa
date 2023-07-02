package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailMobileAnalyseService {
	
	private DateOperation dateOperation = new DateOperation();
	

	/**
	 * 1、查询该用户是否已经有当天的考勤数据，如果有，则进行时间的更新
	 * 
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AttendanceDetail composeAttendanceDetailMobile( EntityManagerContainer emc, String id ) throws Exception {
		String recordDateString = null;
		String empName = null;
		Date signTime = null;
		Date createTime = null;
		Date onDutyTime = null;
		Date offDutyTime = null;
		List<String> ids = null;
		AttendanceDetail attendanceDetail = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		Business business = new Business(emc);
		attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
		if( attendanceDetailMobile != null ){
			createTime = attendanceDetailMobile.getCreateTime();
			if( createTime == null ) {
				createTime = attendanceDetailMobile.getUpdateTime();
			}
			signTime = dateOperation.getDateFromString( attendanceDetailMobile.getSignTime() );
			recordDateString = attendanceDetailMobile.getRecordDateString();
			empName = attendanceDetailMobile.getEmpName();
			ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( empName, recordDateString );
			if( ids != null && !ids.isEmpty() ){
				for( String detailId : ids ){
					attendanceDetail = emc.find( detailId, AttendanceDetail.class );
					if( attendanceDetail != null ){
						emc.beginTransaction( AttendanceDetail.class );
						attendanceDetail.setRecordStatus( 0 );//设置未分析
						if( "上班打卡".equals( attendanceDetailMobile.getSignDescription() )){
							if( attendanceDetail.getOnDutyTime() == null ){
								onDutyTime = dateOperation.getDateFromString( attendanceDetailMobile.getSignTime() );
								if( signTime.before( onDutyTime )){
									attendanceDetail.setOnDutyTime( attendanceDetailMobile.getSignTime() );
								}
							}
						}else{
							//如果不是上班打卡，那么全部更新到下班打卡上
							if( attendanceDetail.getOffDutyTime() == null ){
								offDutyTime = dateOperation.getDateFromString( attendanceDetailMobile.getSignTime() );
								if( signTime.after( offDutyTime )){
									attendanceDetail.setOffDutyTime( attendanceDetailMobile.getSignTime() );
								}
							}
						}
						emc.check( attendanceDetail , CheckPersistType.all );
						emc.commit();
						return attendanceDetail;
					}
				}
			}else{
				//创建一条新的打卡记录
				attendanceDetail = new AttendanceDetail();
				attendanceDetail.setEmpNo( attendanceDetailMobile.getEmpNo() );
				attendanceDetail.setEmpName( attendanceDetailMobile.getEmpName() );
				if( createTime != null ) {
					attendanceDetail.setYearString( dateOperation.getYear( createTime ) );
					attendanceDetail.setMonthString( dateOperation.getMonth( createTime ) );			
				}
				attendanceDetail.setRecordDateString( attendanceDetailMobile.getRecordDateString() );
				if( "上班打卡".equals( attendanceDetailMobile.getSignDescription() )){
					attendanceDetail.setOnDutyTime( attendanceDetailMobile.getSignTime() );
				}else{
					//如果不是上班打卡，那么全部更新到下班打卡上
					attendanceDetail.setOffDutyTime( attendanceDetailMobile.getSignTime() );
				}
				attendanceDetail.setRecordStatus( 0 );
				attendanceDetail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
				
				emc.beginTransaction( AttendanceDetail.class );
				emc.persist( attendanceDetail , CheckPersistType.all );
				emc.commit();
				
				return attendanceDetail;
			}
		}else{
			throw new Exception("record is not exists!id:" + id );
		}
		return null;
	}
}

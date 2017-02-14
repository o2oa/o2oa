package com.x.attendance.assemble.control.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;


public class AttendanceDetailService {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceDetailService.class );

	public AttendanceDetail get( EntityManagerContainer emc, String id ) throws Exception {
		return emc.find(id, AttendanceDetail.class);
	}

	public List<AttendanceDetail> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().list( ids );
	}	
	
	public List<String> listByBatchName( EntityManagerContainer emc, String file_id ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listByBatchName( file_id );
	}

	
	public void checkAndReplenish( EntityManagerContainer emc, List<String> dayList, AttendanceEmployeeConfig attendanceEmployeeConfig) {
		if( dayList == null || dayList.isEmpty() ){
			return;
		}
		if( attendanceEmployeeConfig == null){//不要补录入职以前的数据
			logger.error( "员工信息配置信息为空，无法补录数据。" );
			return;
		}
		if( attendanceEmployeeConfig.getEmpInCompanyTime() == null || attendanceEmployeeConfig.getEmpInCompanyTime().isEmpty() ){
			logger.error( "员工["+attendanceEmployeeConfig.getEmployeeName()+"]的人员配置信息里入职时间为空，无法补录数据。" );
			return;
		}
		DateOperation dateOperation = new DateOperation();
		Business business = null;
		List<String> ids = null;
		AttendanceDetail attendanceDetail = null;
		for( String day : dayList ){
			try {
				if( dateOperation.getDateFromString(day).before( dateOperation.getDateFromString( attendanceEmployeeConfig.getEmpInCompanyTime() )) ){
					logger.error( "不需要补录员工["+attendanceEmployeeConfig.getEmployeeName()+"]入职前的打卡数据。" );
					continue;
				}
				if( dateOperation.getDateFromString( day ).before(dateOperation.getDateFromString(dateOperation.getNowDate()))){
					business = new Business(emc);
					ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceEmployeeConfig.getEmployeeName(), day );
					if( ids == null || ids.isEmpty() ){
						emc.beginTransaction( AttendanceDetail.class );
						//需要补一条打卡数据
						attendanceDetail = new AttendanceDetail();
						attendanceDetail.setEmpNo( attendanceEmployeeConfig.getEmployeeNumber() );
						attendanceDetail.setEmpName( attendanceEmployeeConfig.getEmployeeName() );
						attendanceDetail.setYearString( dateOperation.getYear( dateOperation.getDateFromString(day)) );
						attendanceDetail.setMonthString( dateOperation.getMonth( dateOperation.getDateFromString(day)));
						attendanceDetail.setRecordDateString( day );
						attendanceDetail.setRecordStatus( 0 );
						attendanceDetail.setBatchName( "系统补充" );
						emc.beginTransaction( AttendanceDetail.class );
						emc.persist( attendanceDetail );
						emc.commit();
						logger.info("员工["+attendanceEmployeeConfig.getEmployeeName()+"]在["+day+"]不存在打卡数据，成功补录一条数据。");
					}
				}
			} catch (Exception e) {
				logger.error("系统在核对以及补充员工["+attendanceEmployeeConfig.getEmployeeName()+"]在["+day+"]不存在打卡数据时发生异常！", e);
			}
		}
	}

	public String getMaxRecordDate(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getMaxRecordDate();
	}

	public List<String> listUserAttendanceDetailByYearAndMonth(EntityManagerContainer emc, String q_empName,
			String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUserAttendanceDetailByYearAndMonth( q_empName, q_year, q_month );	
	}

	public List<String> listUserAttendanceDetailByCycleYearAndMonth(EntityManagerContainer emc, String q_empName, String cycleYear,
			String cycleMonth) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUserAttendanceDetailByCycleYearAndMonth( q_empName, cycleYear, cycleMonth );
	}

	public List<String> listCompanyAttendanceDetailByYearAndMonth(EntityManagerContainer emc, List<String> companyNames,
			String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listCompanyAttendanceDetailByYearAndMonth( companyNames, q_year, q_month );	
	}

	public List<String> listDepartmentAttendanceDetailByYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNames, String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listDepartmentAttendanceDetailByYearAndMonth( departmentNames, q_year, q_month );	
	}

	public List<String> getAllAnalysenessDetails( EntityManagerContainer emc, String startDate, String endDate) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessDetails( startDate, endDate );	
	}

	/**
	 * 
	 * @param emc
	 * @param detailId
	 * @param status 申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过
	 * @param autoCommit
	 * @throws Exception
	 */
	public AttendanceDetail updateAppealProcessStatus( EntityManagerContainer emc, String detailId, Integer status, Boolean autoCommit ) throws Exception {
		if( detailId == null ){
			return null;
		}
		AttendanceDetail attendanceDetail = emc.find( detailId, AttendanceDetail.class);
		if( autoCommit ){
			emc.beginTransaction(AttendanceDetail.class);
		}
		if( attendanceDetail != null ){
			attendanceDetail.setAppealStatus( 9 );
			emc.check(attendanceDetail, CheckPersistType.all);
		}
		if( autoCommit ){
			emc.commit();
		}
		
		return attendanceDetail;
	}

	public void archive( EntityManagerContainer emc, String id, String datetime ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id can not be null!");
		}
		AttendanceDetail attendanceDetail = emc.find(id, AttendanceDetail.class);
		emc.beginTransaction( AttendanceDetail.class );
		if( attendanceDetail != null ){
			
		}else{
			throw new Exception("attendance detail info is not exists");
		}
		attendanceDetail.setArchiveTime( datetime );
		emc.check( attendanceDetail, CheckPersistType.all);
		emc.commit();
	}

	public AttendanceDetail save(EntityManagerContainer emc, AttendanceDetail attendanceDetail) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		AttendanceDetail attendanceDetail_old = null;
		Business business = new Business( emc );
		//先看看同一个ID是否存在
		attendanceDetail_old = emc.find( attendanceDetail.getId(), AttendanceDetail.class);
		//如果不存在，则看看该员工当天的打卡信息是否已经存在？
		if( attendanceDetail_old == null ){
			ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceDetail.getEmpName(), attendanceDetail.getRecordDateString() );
			if( ids != null && !ids.isEmpty() ){
				attendanceDetailList = business.getAttendanceDetailFactory().list(ids);
			}
			if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
				attendanceDetail_old = attendanceDetailList.get(0);
			}
		}
		if( attendanceDetail_old != null ){
			//需要进行数据更新
			emc.beginTransaction( AttendanceSetting.class );
			attendanceDetail_old.refresh();
			attendanceDetail_old.setEmpName( attendanceDetail.getEmpName() );
			attendanceDetail_old.setEmpNo( attendanceDetail.getEmpNo() );
			attendanceDetail_old.setRecordDate( attendanceDetail.getRecordDate() );
			attendanceDetail_old.setRecordDateString( attendanceDetail.getRecordDateString() );
			attendanceDetail_old.setRecordStatus( 0 );
			attendanceDetail_old.setOnDutyTime( attendanceDetail.getOnDutyTime() );
			attendanceDetail_old.setOffDutyTime( attendanceDetail.getOnWorkTime() );
			emc.check( attendanceDetail_old, CheckPersistType.all);	
			emc.commit();
		}else{
			//需要新增打卡信息数据
			emc.beginTransaction( AttendanceDetail.class );
			emc.persist( attendanceDetail, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetail;
	}

	public AttendanceDetailMobile save(EntityManagerContainer emc, AttendanceDetailMobile attendanceDetailMobile) throws Exception {
		if( attendanceDetailMobile == null ){
			throw new Exception("attendanceDetailMobile can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		AttendanceDetailMobile attendanceDetailMobile_old = null;
		Business business = new Business( emc );
		//先看看同一个ID是否存在
		attendanceDetailMobile_old = emc.find( attendanceDetailMobile.getId(), AttendanceDetailMobile.class );
		//如果不存在，则看看该员工当天的打卡信息是否已经存在？
		if( attendanceDetailMobile_old == null ){
			ids = business.getAttendanceDetailMobileFactory().listByEmployeeNameDateAndTime( attendanceDetailMobile.getEmpName(), attendanceDetailMobile.getRecordDateString(), attendanceDetailMobile.getSignTime() );
			if( ids != null && !ids.isEmpty() ){
				attendanceDetailMobileList = business.getAttendanceDetailMobileFactory().list(ids);
			}
			if( attendanceDetailMobileList != null && !attendanceDetailMobileList.isEmpty() ){
				attendanceDetailMobile_old = attendanceDetailMobileList.get(0);
			}
		}
		if( attendanceDetailMobile_old != null ){
			//需要进行数据更新
			emc.beginTransaction( AttendanceDetailMobile.class );
			attendanceDetailMobile.copyTo( attendanceDetailMobile_old );
			attendanceDetailMobile_old.setRecordStatus( 0 );
			emc.check( attendanceDetailMobile_old, CheckPersistType.all);	
			emc.commit();
		}else{
			//需要新增打卡信息数据
			emc.beginTransaction( AttendanceDetailMobile.class );
			emc.persist( attendanceDetailMobile, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetailMobile;
	}	
}

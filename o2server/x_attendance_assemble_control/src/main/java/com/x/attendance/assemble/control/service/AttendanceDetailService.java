package com.x.attendance.assemble.control.service;

import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;


public class AttendanceDetailService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailService.class );
	private AttendanceDetailAnalyseService attendanceDetailAnalyseService = new AttendanceDetailAnalyseService();

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

	/**
	 * 根据员工和日期标识，补充不存在的打卡数据信息
	 * @param emc
	 * @param dayList
	 * @param attendanceEmployeeConfig
	 */
	public synchronized void checkAndReplenish( EntityManagerContainer emc, List<String> dayList, AttendanceEmployeeConfig attendanceEmployeeConfig ) {
		if( dayList == null || dayList.isEmpty() ){
			logger.warn( "考勤统计周期内的日期列表为空，无法进行数据补充." );
			return;
		}
		if( attendanceEmployeeConfig == null){//不要补录入职以前的数据
			logger.warn( "员工信息配置信息为空，无法补录数据。" );
			return;
		}
		logger.info( "开始核对和补充员工["+attendanceEmployeeConfig.getEmployeeName()+"]的打卡数据数据......" );
		DateOperation dateOperation = new DateOperation();
		Business business = null;
		List<String> ids = null;
		AttendanceDetail attendanceDetail = null;
		for( String day : dayList ){
			try {
				/*if( dateOperation.getDateFromString(day).before( dateOperation.getDateFromString( attendanceEmployeeConfig.getEmpInTopUnitTime() )) ){
					logger.warn( "不需要补录员工["+attendanceEmployeeConfig.getEmployeeName()+"]入职前的打卡数据。" );
					continue;
				}*/
				if( dateOperation.getDateFromString( day ).before(dateOperation.getDateFromString(dateOperation.getNowDate()))){
					business = new Business(emc);
					ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceEmployeeConfig.getEmployeeName(), day );
					if( ids == null || ids.isEmpty() ){
						emc.beginTransaction( AttendanceDetail.class );
						//需要补一条打卡数据
						attendanceDetail = new AttendanceDetail();
						attendanceDetail.setTopUnitName(attendanceEmployeeConfig.getTopUnitName());
						attendanceDetail.setUnitName(attendanceEmployeeConfig.getUnitName());
						attendanceDetail.setEmpNo( attendanceEmployeeConfig.getEmployeeNumber() );
						attendanceDetail.setEmpName( attendanceEmployeeConfig.getEmployeeName() );
						attendanceDetail.setYearString( dateOperation.getYear( dateOperation.getDateFromString(day)) );
						attendanceDetail.setMonthString( dateOperation.getMonth( dateOperation.getDateFromString(day)));
						attendanceDetail.setRecordDate( dateOperation.getDateFromString( day ) );
						attendanceDetail.setRecordDateString( day );
						attendanceDetail.setRecordStatus( 0 );
						attendanceDetail.setBatchName( "系统补充" );
						emc.beginTransaction( AttendanceDetail.class );
						emc.persist( attendanceDetail );
						emc.commit();
						logger.info("员工["+attendanceEmployeeConfig.getEmployeeName()+"]在["+day+"]不存在打卡数据，成功补录一条数据。");
					}else{
						for(String id :ids){
							AttendanceDetail detail = this.get(emc, id );
							if(detail !=null){
								detail.setTopUnitName(attendanceEmployeeConfig.getTopUnitName());
								detail.setUnitName(attendanceEmployeeConfig.getUnitName());
								emc.beginTransaction( AttendanceDetail.class );
								emc.persist( detail , CheckPersistType.all );
								emc.commit();
							}
						}
					}
				}else {
					logger.warn( "员工["+attendanceEmployeeConfig.getEmployeeName()+"]的打卡数据数据["+day+"], 目前还不能补录." );
				}
			} catch (Exception e) {
				logger.warn("系统在核对以及补充员工["+attendanceEmployeeConfig.getEmployeeName()+"]在["+day+"]不存在打卡数据时发生异常！" );
				logger.error(e);
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

	public List<String> listDetailByCycleYearAndMonthWithOutStatus( EntityManagerContainer emc, String user, String year, String month )  throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listDetailByCycleYearAndMonthWithOutStatus( user, year, month );
	}
	
	//根据人员和打卡日期查找打卡记录  
	public List<String> listDetailByNameAndDate( EntityManagerContainer emc, String user, String datestr )  throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listDetailByNameAndDate( user, datestr );
	}

	public List<String> listUserAttendanceDetailByCycleYearAndMonth(EntityManagerContainer emc, String q_empName, String cycleYear,
			String cycleMonth) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUserAttendanceDetailByCycleYearAndMonth( q_empName, cycleYear, cycleMonth );
	}

	public List<String> listTopUnitAttendanceDetailByYearAndMonth(EntityManagerContainer emc, List<String> topUnitNames,
			String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listTopUnitAttendanceDetailByYearAndMonth( topUnitNames, q_year, q_month );	
	}

	public List<String> listUnitAttendanceDetailByYearAndMonth(EntityManagerContainer emc,
			List<String> unitNames, String q_year, String q_month) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listUnitAttendanceDetailByYearAndMonth( unitNames, q_year, q_month );	
	}

	public List<String> getAllAnalysenessDetails( EntityManagerContainer emc, String startDate, String endDate, String personName ) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessDetails( startDate, endDate, personName );	
	}
	
	public List<String> getAllAnalysenessDetailsForce( EntityManagerContainer emc, String startDate, String endDate, String personName ,Boolean forceFlag) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessDetailsForce( startDate, endDate, personName ,forceFlag);
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
			if( status != 1 ) {
				attendanceDetail.setAppealProcessor( null );
			}
			attendanceDetail.setAppealStatus( status );
			if(status == 9){
				//若申述通过则更新Detail状态，使得Detail为正常打卡
				attendanceDetail.setIsGetSelfHolidays(false);
				attendanceDetail.setIsLate(false);
				attendanceDetail.setIsAbsent(false);
				attendanceDetail.setIsAbnormalDuty(false);
				attendanceDetail.setIsLackOfTime(false);
				attendanceDetail.setIsLeaveEarlier(false);
				//并对该条考勤数据发起统计请求
				attendanceDetailAnalyseService.recordStatisticRequireLog(attendanceDetail,true);
			}
			emc.check( attendanceDetail, CheckPersistType.all );

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

	public AttendanceDetail save( EntityManagerContainer emc, AttendanceDetail attendanceDetail ) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		AttendanceDetail attendanceDetail_old = null;
		Business business = new Business( emc );
		
		//如果不存在，则看看该员工当天的打卡信息是否已经存在？
		ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceDetail.getEmpName(), attendanceDetail.getRecordDateString() );
		if( ids != null && !ids.isEmpty() ){
			attendanceDetailList = business.getAttendanceDetailFactory().list(ids);
		}
		if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
			attendanceDetail_old = attendanceDetailList.get(0);
		}
		
		if( attendanceDetail_old != null ){
			//需要进行数据更新			
			attendanceDetail_old.refresh();
			attendanceDetail_old.setEmpName( attendanceDetail.getEmpName() );
			attendanceDetail_old.setEmpNo( attendanceDetail.getEmpNo() );
			attendanceDetail_old.setRecordDate( attendanceDetail.getRecordDate() );
			attendanceDetail_old.setRecordDateString( attendanceDetail.getRecordDateString() );
			attendanceDetail_old.setRecordStatus( 0 );
			if(attendanceDetail.getOnDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOnDutyTime( attendanceDetail.getOnDutyTime() );
			}
			if(attendanceDetail.getMorningOffDutyTime()!= null && attendanceDetail.getMorningOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setMorningOffDutyTime( attendanceDetail.getMorningOffDutyTime() );
			}
			if(attendanceDetail.getAfternoonOnDutyTime()!= null && attendanceDetail.getAfternoonOnDutyTime().trim().length() > 0){
				attendanceDetail_old.setAfternoonOnDutyTime( attendanceDetail.getAfternoonOnDutyTime() );
			}
			if(attendanceDetail.getOffDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOffDutyTime( attendanceDetail.getOffDutyTime() );
			}
			
			//emc.beginTransaction( AttendanceSetting.class );
			emc.beginTransaction( AttendanceDetail.class );
			emc.check( attendanceDetail_old, CheckPersistType.all);	
			emc.commit();
			attendanceDetail = attendanceDetail_old;
		}else{
			//需要新增打卡信息数据
			if( attendanceDetail.getId() == null ) {
				attendanceDetail.setId( AttendanceDetail.createId() );
			}
			emc.beginTransaction( AttendanceDetail.class );
			emc.persist( attendanceDetail, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetail;
	}
	
	public AttendanceDetail saveSingle( EntityManagerContainer emc, AttendanceDetail attendanceDetail ) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail can not be null!");
		}
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		AttendanceDetail attendanceDetail_old = null;
		Business business = new Business( emc );
		
		//如果不存在，则看看该员工当天的打卡信息是否已经存在？
		ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( attendanceDetail.getEmpName(), attendanceDetail.getRecordDateString() );
		if( ids != null && !ids.isEmpty() ){
			attendanceDetailList = business.getAttendanceDetailFactory().list(ids);
		}
		if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
			attendanceDetail_old = attendanceDetailList.get(0);
		}
		
		if( attendanceDetail_old != null ){
			//需要进行数据更新			
			attendanceDetail_old.refresh();
			attendanceDetail_old.setEmpName( attendanceDetail.getEmpName() );
			attendanceDetail_old.setEmpNo( attendanceDetail.getEmpNo() );
			attendanceDetail_old.setRecordDate( attendanceDetail.getRecordDate() );
			attendanceDetail_old.setRecordDateString( attendanceDetail.getRecordDateString() );
			attendanceDetail_old.setRecordStatus( 0 );
			if(attendanceDetail.getOnDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOnDutyTime( attendanceDetail.getOnDutyTime() );
			}
			if(attendanceDetail.getMorningOffDutyTime()!= null && attendanceDetail.getMorningOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setMorningOffDutyTime( attendanceDetail.getMorningOffDutyTime() );
			}
			if(attendanceDetail.getAfternoonOnDutyTime()!= null && attendanceDetail.getAfternoonOnDutyTime().trim().length() > 0){
				attendanceDetail_old.setAfternoonOnDutyTime( attendanceDetail.getAfternoonOnDutyTime() );
			}
			if(attendanceDetail.getOffDutyTime()!= null && attendanceDetail.getOffDutyTime().trim().length() > 0){
				attendanceDetail_old.setOffDutyTime( attendanceDetail.getOffDutyTime() );
			}
			 
			 
			//emc.beginTransaction( AttendanceSetting.class );
			emc.beginTransaction( AttendanceDetail.class );
			emc.check( attendanceDetail_old, CheckPersistType.all);	
			emc.commit();
			attendanceDetail = attendanceDetail_old;
		}else{
			//需要新增打卡信息数据
			if( attendanceDetail.getId() == null ) {
				attendanceDetail.setId( AttendanceDetail.createId() );
			}
			emc.beginTransaction( AttendanceDetail.class );
			emc.persist( attendanceDetail, CheckPersistType.all);	
			emc.commit();
		}
		return attendanceDetail;
	}
	


	public AttendanceDetailMobile save( EntityManagerContainer emc, AttendanceDetailMobile attendanceDetailMobile ) throws Exception {
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
			attendanceDetailMobile.copyTo( attendanceDetailMobile_old, JpaObject.FieldsUnmodify );
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

	public List<String> getAllAnalysenessPersonNames(EntityManagerContainer emc, String startDate, String endDate) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessPersonNames( startDate, endDate );
	}
	
	public List<String> getAllAnalysenessPersonNamesForce(EntityManagerContainer emc, String startDate, String endDate,Boolean forceFlag) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().getAllAnalysenessPersonNamesForce( startDate, endDate , forceFlag);
	}

	public AttendanceDetail listDetailWithEmployee(EntityManagerContainer emc, String employeeName, String recordDateString) throws Exception {
		Business business =  new Business( emc );
		List<AttendanceDetail> details =  business.getAttendanceDetailFactory().listDetailByEmployeeNameAndDate(employeeName, recordDateString);
		if( ListTools.isNotEmpty( details )) {
			return  details.get(0);
		}else {
			return null;
		}
	}

    public List<String> listRecordWithDateAndNoOffDuty(EntityManagerContainer emc, String dateString) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listRecordWithDateAndNoOffDuty( dateString );
    }

	/**
	 * 查询在指定截止日期前已经打过卡的人员
	 * @param deadline
	 * @param type:all#onDuty#offDuty#morningOffDuty#afternoonOnDuty
	 * @return
	 * @throws Exception
	 */
    public List<String> listSignedPersonsWithDeadLine(EntityManagerContainer emc, String deadline, String type) throws Exception {
		Business business =  new Business( emc );
		return business.getAttendanceDetailFactory().listSignedPersonsWithDeadLine( deadline, type );
    }
}

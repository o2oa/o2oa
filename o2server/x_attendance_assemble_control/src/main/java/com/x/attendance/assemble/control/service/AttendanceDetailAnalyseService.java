package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.AttendanceCycles;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseService.class );
	private AttendanceSelfHolidayService attendanceSelfHolidayService = new AttendanceSelfHolidayService();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();
	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();
	private DateOperation dateOperation = new DateOperation();
	private UserManagerService userManagerService = new UserManagerService();
	
	/**
	 * 根据员工姓名，开始日期和结束日期获取日期范围内所有的打卡记录信息ID列表，从开始日期0点，到结束日期23点59分
	 * @param empName
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> getAnalyseAttendanceDetailIds( EntityManagerContainer emc, String empName, Date startDate, Date endDate )throws Exception{		
		Business business = null;
		List<String> ids = null;
		String startDateString = null, endDateString = null;
		business = new Business(emc);
		startDateString = dateOperation.getDateStringFromDate( startDate, "yyyy-MM-dd" ) + " 00:00:00";
		endDateString = dateOperation.getDateStringFromDate( endDate, "yyyy-MM-dd" ) + "23:59:59";
		ids = business.getAttendanceDetailFactory().getUserAnalysenessDetails( empName, startDateString, endDateString);
		return ids;
	}
	
	/**
     * 根据员工姓名，开始日期和结束日期重新分析所有的打卡记录
     * 一般在用户补了休假记录的时候使用，补充了休假记录，需要将休假日期范围内的所有打卡记录重新分析，并且触发相关月份的统计
     * @param empName
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
	public Boolean analyseAttendanceDetails( EntityManagerContainer emc, String empName, Date startDate, Date endDate, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		Business business = null;
		List<String> ids = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		List<AttendanceDetail> attendanceDetailList = null;
		
		business = new Business(emc);
		ids = getAnalyseAttendanceDetailIds( emc, empName, startDate, endDate );
		if( ids == null || ids.isEmpty() ){
			logger.debug( debugger, ">>>>>>>>>>attendance detail info ids not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}
		attendanceDetailList = business.getAttendanceDetailFactory().list( ids );
		if( attendanceDetailList == null || attendanceDetailList.isEmpty() ){
			logger.debug( debugger, ">>>>>>>>>>attendance detail info not exists for emp{'empName':'"+empName+"','startDate':'"+startDate+"','endDate':'"+endDate+"'}");
			return true;
		}
		attendanceWorkDayConfigList = business.getAttendanceWorkDayConfigFactory().listAll();
		for( AttendanceDetail detail : attendanceDetailList ){
			try{
				analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );	
			}catch(Exception e){
				logger.info( "employee attenance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]");
				logger.error(e);
			}
		}
		logger.info( "employee attendance details by empname, startdate and end date analyse over！" );;
		return true;
	}
	
	/**
	 * 对一组的打卡信息数据进行分析
	 * @param detailList
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetails( EntityManagerContainer emc, List<AttendanceDetail> detailList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		if( detailList == null || detailList.isEmpty() ){
			return true;
		}
		Business business = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		
		business = new Business(emc);
		attendanceWorkDayConfigList = business.getAttendanceWorkDayConfigFactory().listAll();
		for( AttendanceDetail detail : detailList ){			
			try{
				analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );
			}catch(Exception e){
				logger.info( "employee attendance detail analyse got an exception:["+detail.getEmpName()+"]["+detail.getRecordDateString()+"]" );
				logger.error(e);
			}
		}
		logger.info( "employee attendance detail analyse over！" );
		return true;
	}

	/**
	 * 对单条的打卡数据分析统计周期
	 * 包装成List使用另一个方法进行操作：analyseAttendanceDetails
	 * 
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	public AttendanceDetail analyseAttendanceDetailStatisticCycle( AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		//根据打卡信息所属个人的组织获取一个合适的统计周期对象		
		attendanceStatisticalCycle = attendanceStatisticalCycleService.getStatisticCycleWithStartAndEnd(
				detail.getTopUnitName(), 
				detail.getUnitName(), 
				detail.getRecordDate(), 
				topUnitAttendanceStatisticalCycleMap,
				debugger
		);
		if( attendanceStatisticalCycle != null ) {
			logger.debug( debugger, ">>>>>>>>>>日期" + detail.getRecordDateString() + ", 周期" + attendanceStatisticalCycle.getCycleStartDateString() + "-" + attendanceStatisticalCycle.getCycleEndDateString() );
			detail.setCycleYear( attendanceStatisticalCycle.getCycleYear() );
			detail.setCycleMonth( attendanceStatisticalCycle.getCycleMonth() );
		}
		return detail;
	}
	
	/**
	 * 对单条的打卡数据进行分析
	 * 包装成List使用另一个方法进行操作：analyseAttendanceDetails
	 * @param emc
	 * @param detail
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		if( detail != null ){
			List<AttendanceDetail> detailList = new ArrayList<AttendanceDetail>();
			detailList.add( detail );
			return analyseAttendanceDetails( emc, detailList, topUnitAttendanceStatisticalCycleMap, debugger );
		}else{
			logger.info( "detail为空，无法继续进行分析。" );
		}
		return false;
	}

	/**
	 * 对单条的打卡数据进行详细分析
	 * @param emc
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail,
			List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, 
			List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception{
		List<String> ids_temp = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		List<AttendanceSelfHoliday> attendanceSelfHolidayList = null;
		if( detail != null ){
			try{
				ids_temp = attendanceSelfHolidayService.getByPersonName( emc, detail.getEmpName() );
				if( ids_temp != null && !ids_temp.isEmpty() ) {
					attendanceSelfHolidayList = attendanceSelfHolidayService.list( emc, ids_temp );
				}
			}catch( Exception e ){
				logger.warn( "system list attendance self holiday info ids with employee name got an exception.empname:" + detail.getEmpName() );
				logger.error(e);
			}
			
			attendanceScheduleSetting = attendanceScheduleSettingService.getAttendanceScheduleSettingWithPerson( detail.getEmpName(), debugger );
			
			return analyseAttendanceDetail( emc, detail, attendanceScheduleSetting, attendanceSelfHolidayList, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );

		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}
	
	/**
	 * 对单条的打卡数据进行详细分析
	 * @param emc
	 * @param detail
	 * @param attendanceScheduleSetting
	 * @param attendanceSelfHolidayList
	 * @param attendanceWorkDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 * @return
	 * @throws Exception
	 */
	public Boolean analyseAttendanceDetail( EntityManagerContainer emc, AttendanceDetail detail, 
			AttendanceScheduleSetting attendanceScheduleSetting, List<AttendanceSelfHoliday> attendanceSelfHolidayList, 
			List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
			Boolean debugger ) throws Exception{
		if( detail != null ){
			DateOperation dateOperation = new DateOperation();
			logger.debug( debugger, "attendance detail analysing, employee name:["+detail.getEmpName()+"]-["+detail.getRecordDateString()+"]......" );		
			Boolean check = true;
			Date recordDate = null;
			
			if( attendanceScheduleSetting == null ){
				check = false;
				saveAnalyseResultAndStatus( emc, detail.getId(), -1, "未查询到员工[" + detail.getEmpName() + "]所在组织的排班信息" );
			}
			
			if( check ){
				if( detail.getRecordDateString() != null && detail.getRecordDateString().length() > 0 ){
					try{
						recordDate = dateOperation.getDateFromString( detail.getRecordDateString());
					}catch(Exception e){
						saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统格式化记录的打卡日期发生异常，未能设置日期格式的打卡时间属性recordDate，日期recordDateString：" + detail.getRecordDateString() );
						check = false;
					}
				}else{
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统格式化记录的打卡日期发生异常，未能设置日期格式的打卡时间属性为空" );
					check = false;
				}
			}
			
			if( check ){
				detail = emc.find( detail.getId(), AttendanceDetail.class );//要重新查询一次，才可能在这个emc里进行管理
				if( detail != null ){
					detail.refresh(); //将打卡数据里分析过的状态全部清空，还原成未分析的数据
				}else{
					check = false;
					logger.warn( "system can not find detail, record may be deleted." );
				}
			}
			
			if( check ){
				if( attendanceScheduleSetting != null ){
					detail.setTopUnitName( attendanceScheduleSetting.getTopUnitName() );
					if( StringUtils.isNotEmpty( attendanceScheduleSetting.getUnitName() ) && !"*".equals( attendanceScheduleSetting.getUnitName() )) {
						detail.setUnitName( attendanceScheduleSetting.getUnitName() );
					}else {
						detail.setUnitName( userManagerService.getUnitNameWithPersonName( detail.getEmpName() ) );
					}
					detail.setOnWorkTime( attendanceScheduleSetting.getOnDutyTime() );
					detail.setOffWorkTime( attendanceScheduleSetting.getOffDutyTime() );
				}
				if( recordDate != null ){
					detail.setRecordDate( recordDate );
				}
			}
			if( check ){
				try{
					setSelfHolidays( detail, attendanceSelfHolidayList, dateOperation, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse employee self holiday for detail got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息，请假信息分析员工请假情况时发生异常" );
				}
			}
			if( check ){
				try{
					detail.setIsWeekend( dateOperation.isWeekend( detail.getRecordDate() ));
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be weekend got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是周末时发生异常, recordDate:" + detail.getRecordDateString() );
				}
			}
			if( check ){
				try{
					detail.setIsHoliday( isHoliday( detail, attendanceWorkDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be holiday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是节假日时发生异常, recordDate:"+ detail.getRecordDateString() );
				}
			}
			if( check ){
				try{
					detail.setIsWorkday( isWorkday( detail, attendanceWorkDayConfigList, dateOperation ) );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse record date may be workday got an exception." + detail.getRecordDateString() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在分析打卡日期是否是工作日时发生异常" );
				}
			}	
			if( check ){
				try{
					analyseAttendanceDetailStatisticCycle( detail, topUnitAttendanceStatisticalCycleMap, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail statistic cycle got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息以及排班信息进一步分析打卡信息统计周期时发生异常" );
				}
			}
			if( check ){
				try{
					analyseAttendanceDetail( detail, attendanceScheduleSetting, dateOperation, debugger );
				}catch( Exception e ){
					check = false;
					logger.warn( "system analyse detail by on and off work time for advance analyse got an exception." + detail.getEmpName() );
					logger.error(e);
					saveAnalyseResultAndStatus( emc, detail.getId(), -1, "系统在根据打卡信息排班信息进一步分析员工出勤情况时发生异常" );
				}
			}
			if( check ){
				emc.beginTransaction( AttendanceDetail.class );
				detail.setRecordStatus( 1 );
				detail.setDescription("员工打卡记录分析完成！" );
				emc.check( detail, CheckPersistType.all );
				emc.commit();
			}
			if( check ){
				//分析成功根据打卡数据来记录与这条数据相关的统计需求记录
				recordStatisticRequireLog( detail, debugger );
			}
			return true;
		}else{
			logger.warn( "attendance detail is null, system can not analyse." );
		}
		return false;
	}
	/**
	 * 根据打卡数据来记录与这条数据相关的统计需求记录
	 * @param detail
	 * @throws Exception 
	 */
	public void recordStatisticRequireLog( AttendanceDetail detail, Boolean debugger ) throws Exception{
		//数据分析完成，那么需要记录一下需要统计的信息数据
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		Business business = null;
		List<AttendanceStatisticRequireLog> logList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			attendanceStatisticRequireLogFactory = business.getAttendanceStatisticRequireLogFactory();
			//个人统计每月统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("PERSON_PER_MONTH", detail.getEmpName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( logList == null || logList.size() == 0 ){
				logger.debug( debugger, ">>>>>>>>>>统计数据不存在：PERSON_PER_MONTH，"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("员工每月统计");
				attendanceStatisticRequireLog.setStatisticType("PERSON_PER_MONTH");
				attendanceStatisticRequireLog.setStatisticKey( detail.getEmpName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				logger.debug( debugger, ">>>>>>>>>>统计数据已存在：PERSON_PER_MONTH，"+ detail.getEmpName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//组织按月统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_MONTH", detail.getUnitName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( logList == null || logList.size() == 0 ){
				logger.debug( debugger, ">>>>>>>>>>统计数据不存在：UNIT_PER_MONTH，"+ detail.getUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("组织每月统计");
				attendanceStatisticRequireLog.setStatisticType("UNIT_PER_MONTH");
				attendanceStatisticRequireLog.setStatisticKey( detail.getUnitName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				logger.debug( debugger, ">>>>>>>>>>统计数据已存在：UNIT_PER_MONTH，"+ detail.getUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//顶层组织按月统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_MONTH", detail.getTopUnitName(), detail.getCycleYear(), detail.getCycleMonth(), null, "WAITING");
			if( logList == null || logList.size() == 0 ){
				logger.debug( debugger, ">>>>>>>>>>统计数据不存在：TOPUNIT_PER_MONTH，"+ detail.getTopUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("顶层组织每月统计");
				attendanceStatisticRequireLog.setStatisticType("TOPUNIT_PER_MONTH");
				attendanceStatisticRequireLog.setStatisticKey( detail.getTopUnitName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				logger.debug( debugger, ">>>>>>>>>>统计数据已存在：TOPUNIT_PER_MONTH，"+ detail.getTopUnitName() + ", " + detail.getCycleYear() + ", " + detail.getCycleMonth() + ", null, WAITING" );
			}
			//组织每日统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_DAY", detail.getUnitName(), null, null, detail.getRecordDateString(), "WAITING");
			if( logList == null || logList.size() == 0 ){
				logger.debug( debugger, ">>>>>>>>>>统计数据不存在：UNIT_PER_DAY，"+ detail.getUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("组织每日统计");
				attendanceStatisticRequireLog.setStatisticType("UNIT_PER_DAY");
				attendanceStatisticRequireLog.setStatisticKey( detail.getUnitName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				attendanceStatisticRequireLog.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				logger.debug( debugger, ">>>>>>>>>>统计数据已存在：UNIT_PER_DAY，"+ detail.getUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
			//顶层组织每日统计
			logList = null;
			logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_DAY", detail.getTopUnitName(), null, null, detail.getRecordDateString(), "WAITING");
			if( logList == null || logList.size() == 0 ){
				logger.debug( debugger, ">>>>>>>>>>统计数据不存在：TOPUNIT_PER_DAY，"+ detail.getTopUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
				attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				attendanceStatisticRequireLog.setStatisticName("顶层组织每日统计");
				attendanceStatisticRequireLog.setStatisticType("TOPUNIT_PER_DAY");
				attendanceStatisticRequireLog.setStatisticKey( detail.getTopUnitName() );
				attendanceStatisticRequireLog.setStatisticYear( detail.getCycleYear() );
				attendanceStatisticRequireLog.setStatisticMonth( detail.getCycleMonth() );
				attendanceStatisticRequireLog.setStatisticDay( detail.getRecordDateString() );
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
				emc.commit();
			}else{
				logger.debug( debugger, ">>>>>>>>>>统计数据已存在：TOPUNIT_PER_DAY，"+ detail.getTopUnitName() + ", null, null , "+ detail.getRecordDateString() +", WAITING" );
			}
		}catch(Exception e){
			logger.warn("系统在向数据库新增统计需求时发生异常" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据员工休假数据来记录与这条数据相关的统计需求记录
	 * @param detail
	 * @throws Exception 
	 */
	public void recordStatisticRequireLog( AttendanceSelfHoliday holiday, Boolean debugger ) throws Exception{
		//数据分析完成，那么需要记录一下需要统计的信息数据
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		Business business = null;
		List<AttendanceStatisticRequireLog> logList = null;
		List<AttendanceCycles> cycleList = null;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//查询员工请假期间有几个统计周期
			cycleList = business.getAttendanceDetailFactory().getCyclesFromDetailWithDateSplit( holiday.getEmployeeName(), holiday.getStartTime(), holiday.getEndTime());
		}catch(Exception e){
			logger.warn("系统在查询员工请假期间有几个统计周期时发生异常" );
			logger.error(e);
		}
		
		if( cycleList != null && cycleList.size() > 0 ){
			for( AttendanceCycles attendanceCycles : cycleList){
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					attendanceStatisticRequireLogFactory = business.getAttendanceStatisticRequireLogFactory();
					//个人统计每月统计
					logList = null;
					logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("PERSON_PER_MONTH", holiday.getEmployeeName(), attendanceCycles.getCycleYear(), attendanceCycles.getCycleMonth(), null, "WAITING");
					if( logList == null || logList.size() == 0 ){
						logger.debug( debugger, ">>>>>>>>>>统计数据不存在：PERSON_PER_MONTH，"+ holiday.getEmployeeName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
						attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
						attendanceStatisticRequireLog.setStatisticName("员工每月统计");
						attendanceStatisticRequireLog.setStatisticType("PERSON_PER_MONTH");
						attendanceStatisticRequireLog.setStatisticKey( holiday.getEmployeeName() );
						attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
						attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						logger.debug( debugger, ">>>>>>>>>>统计数据已存在：PERSON_PER_MONTH，"+ holiday.getEmployeeName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
					}
					//组织按月统计
					logList = null;
					logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_MONTH", holiday.getUnitName(), attendanceCycles.getCycleYear(), attendanceCycles.getCycleMonth(), null, "WAITING");
					if( logList == null || logList.size() == 0 ){
						logger.debug( debugger, ">>>>>>>>>>统计数据不存在：UNIT_PER_MONTH，"+ holiday.getUnitName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
						attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
						attendanceStatisticRequireLog.setStatisticName("组织每月统计");
						attendanceStatisticRequireLog.setStatisticType("UNIT_PER_MONTH");
						attendanceStatisticRequireLog.setStatisticKey( holiday.getUnitName() );
						attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
						attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						logger.debug( debugger, ">>>>>>>>>>统计数据已存在：UNIT_PER_MONTH，"+ holiday.getUnitName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
					}
					//顶层组织按月统计
					logList = null;
					logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_MONTH", holiday.getTopUnitName(), attendanceCycles.getCycleYear(), attendanceCycles.getCycleMonth(), null, "WAITING");
					if( logList == null || logList.size() == 0 ){
						logger.debug( debugger, ">>>>>>>>>>统计数据不存在：TOPUNIT_PER_MONTH，"+ holiday.getTopUnitName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
						attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
						attendanceStatisticRequireLog.setStatisticName("顶层组织每月统计");
						attendanceStatisticRequireLog.setStatisticType("TOPUNIT_PER_MONTH");
						attendanceStatisticRequireLog.setStatisticKey( holiday.getTopUnitName() );
						attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
						attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						logger.debug( debugger, ">>>>>>>>>>统计数据已存在：TOPUNIT_PER_MONTH，"+ holiday.getTopUnitName() + ", " + attendanceCycles.getCycleYear() + ", " + attendanceCycles.getCycleMonth() + ", null, WAITING" );
					}
				}catch(Exception e){
					logger.warn("系统在向数据库新增每月统计需求时发生异常" );
					logger.error(e);
				}
			}
		}
		List<String> dateList = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//查询员工请假期间有几多少天
			DateOperation dateOperation = new DateOperation();
			dateList = dateOperation.listDateStringBetweenDate( holiday.getStartTime(), holiday.getEndTime() );
		}catch(Exception e){
			logger.warn("系统在查询员工请假期间有几多少天时发生异常" );
			logger.error(e);
		}
		DateOperation dateOperation = new DateOperation();
		if( dateList != null ){
			for( String date : dateList ){
				
				try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
					business = new Business(emc);
					//查询员工请假期间有几个统计周期
					cycleList = business.getAttendanceDetailFactory().getCyclesFromDetailWithDateSplit( holiday.getEmployeeName(), dateOperation.getDateFromString(date + " 00:00:00"), dateOperation.getDateFromString(date+ " 23:59:59"));
				}catch(Exception e){
					logger.warn("系统在查询员工请假期间有几个统计周期时发生异常" );
					logger.error(e);
				}
				
				if( cycleList !=  null && cycleList.size() > 0 ){
					for( AttendanceCycles attendanceCycles : cycleList ){
						try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
							business = new Business(emc);
							//组织每日统计
							logList = null;
							logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("UNIT_PER_DAY", holiday.getUnitName(), null, null, date, "WAITING");
							if( logList == null || logList.size() == 0 ){
								logger.debug( debugger, ">>>>>>>>>>统计数据不存在：UNIT_PER_DAY，"+ holiday.getUnitName() + ", null, null , "+ date +", WAITING" );
								attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
								attendanceStatisticRequireLog.setStatisticName("组织每日统计");
								attendanceStatisticRequireLog.setStatisticType("UNIT_PER_DAY");
								attendanceStatisticRequireLog.setStatisticKey( holiday.getUnitName() );
								attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
								attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
								attendanceStatisticRequireLog.setStatisticDay( date );
								emc.beginTransaction( AttendanceStatisticRequireLog.class );
								emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
								emc.commit();
							}else{
								logger.debug( debugger, ">>>>>>>>>>统计数据已存在：UNIT_PER_DAY，"+ holiday.getUnitName() + ", null, null , "+ date +", WAITING" );
							}
							//顶层组织每日统计
							logList = null;
							logList = attendanceStatisticRequireLogFactory.getByNameKeyAndStatus("TOPUNIT_PER_DAY", holiday.getTopUnitName(), null, null, date, "WAITING");
							if( logList == null || logList.size() == 0 ){
								logger.debug( debugger, ">>>>>>>>>>统计数据不存在：TOPUNIT_PER_DAY，"+ holiday.getTopUnitName() + ", null, null , "+ date +", WAITING" );
								attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
								attendanceStatisticRequireLog.setStatisticName("顶层组织每日统计");
								attendanceStatisticRequireLog.setStatisticType("TOPUNIT_PER_DAY");
								attendanceStatisticRequireLog.setStatisticKey( holiday.getTopUnitName() );
								attendanceStatisticRequireLog.setStatisticYear( attendanceCycles.getCycleYear() );
								attendanceStatisticRequireLog.setStatisticMonth( attendanceCycles.getCycleMonth() );
								attendanceStatisticRequireLog.setStatisticDay( date );
								emc.beginTransaction( AttendanceStatisticRequireLog.class );
								emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
								emc.commit();
							}else{
								logger.debug( debugger, ">>>>>>>>>>统计数据已存在：TOPUNIT_PER_DAY，"+ holiday.getTopUnitName() + ", null, null , "+ date +", WAITING" );
							}
						}catch(Exception e){
							logger.warn("系统在向数据库新增每日统计需求时发生异常" );
							logger.error(e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 判断当前日期是否为调休工作日
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @return
	 */
	private boolean isWorkday(AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, DateOperation dateOperation) {
		if( attendanceWorkDayConfigList != null ){
			Date configDate = null;
			for( AttendanceWorkDayConfig workDayConfig : attendanceWorkDayConfigList ){
				try {
					configDate = dateOperation.getDateFromString( workDayConfig.getConfigDate() );
				} catch (Exception e) {
					logger.warn( "系统转换"+workDayConfig.getConfigDate()+"格式为日期时发生异常！" );
					logger.error(e);
				}
				//进行日期对比
				Calendar calendar_record = Calendar.getInstance();
				Calendar calendar_config = Calendar.getInstance();
				calendar_record.setTime( detail.getRecordDate() );
				calendar_config.setTime( configDate );				
				if( calendar_record.get( Calendar.YEAR ) == calendar_config.get( Calendar.YEAR )
					&& calendar_record.get( Calendar.MONTH ) == calendar_config.get( Calendar.MONTH )
					&& calendar_record.get( Calendar.DATE ) == calendar_config.get( Calendar.DATE )
					&& "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
				){
					return true;
				}
			}
		}	
		return false;
	}

	/**
	 * 判断当前日期是否为法定节假日
	 * @param detail
	 * @param attendanceWorkDayConfigList
	 * @return
	 */
	private boolean isHoliday( AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, DateOperation dateOperation) {
		if( attendanceWorkDayConfigList != null ){
			Date configDate = null;
			for( AttendanceWorkDayConfig workDayConfig : attendanceWorkDayConfigList ){
				try {
					configDate = dateOperation.getDateFromString( workDayConfig.getConfigDate() );
				} catch (Exception e) {
					logger.warn( "系统转换"+workDayConfig.getConfigDate()+"格式为日期时发生异常！" );
					logger.error(e);
				}
				//进行日期对比
				Calendar calendar_record = Calendar.getInstance();
				Calendar calendar_config = Calendar.getInstance();
				calendar_record.setTime( detail.getRecordDate() );
				calendar_config.setTime( configDate );				
				if( calendar_record.get( Calendar.YEAR ) == calendar_config.get( Calendar.YEAR )
					&& calendar_record.get( Calendar.MONTH ) == calendar_config.get( Calendar.MONTH )
					&& calendar_record.get( Calendar.DATE ) == calendar_config.get( Calendar.DATE )
					&& "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
				){
					return true;
				}
			}
		}	
		return false;
	}
	
	/**
	 * 判断员工是否正在休假以及休假的时段
	 * @param recordDate
	 * @param attendanceSelfHolidayList 
	 * @return
	 * @throws Exception 
	 */
	private void setSelfHolidays( AttendanceDetail detail, List<AttendanceSelfHoliday> attendanceSelfHolidayList, DateOperation dateOperation, Boolean debugger ) throws Exception {
		//查询当前打卡用户所有的请假申请中，打卡时间是否处于休假时间段中
		Date dayWorkStart = null, dayWorkEnd = null, dayMiddle = null ;
		if( attendanceSelfHolidayList != null ){
			//如果打卡日期全天 在 休假时间的中间，那么是全天请假
			try {
				dayWorkStart = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOnWorkTime());
			} catch (Exception e) {
				logger.warn( "setSelfHolidays获取打卡日期的一天的工作开始时间发生异常。" );
				logger.error(e);
				return;
			}
			try {
				dayWorkEnd = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " " + detail.getOffWorkTime() );
			} catch (Exception e) {
				logger.warn( "setSelfHolidays获取打卡日期的一天的工作结束时间发生异常。" );
				logger.error(e);
				return;
			}
			try {
				dayMiddle = dateOperation.getDateFromString( dateOperation.getDateStringFromDate(detail.getRecordDate(), "yyyy-MM-dd") + " 12:00:00" );
			} catch (Exception e) {
				logger.warn( "setSelfHolidays获取打卡日期的一天的工作午休时间发生异常。" );
				logger.error(e);
				return;
			}
			//循环比对，看看是否全天请假
			for( AttendanceSelfHoliday selfHoliday : attendanceSelfHolidayList ){
				if( selfHoliday.getEndTime().getTime() > dayWorkStart.getTime() && selfHoliday.getStartTime().getTime() < dayWorkEnd.getTime() ){
					//对比员工姓名，员工姓名是唯一键
					if( detail.getEmpName().equals( selfHoliday.getEmployeeName() ) ){
						if( selfHoliday.getStartTime().getTime() <= dayWorkStart.getTime() && selfHoliday.getEndTime().getTime() >= ( dayMiddle.getTime() + 2*60*60*1000 )){
							logger.debug( debugger, ">>>>>>>>>>"+detail.getEmpName()+"全天请假了");
							//全天休假
							detail.setIsGetSelfHolidays(true);
							detail.setSelfHolidayDayTime("全天");
							detail.setGetSelfHolidayDays(1.0);
						}else if( selfHoliday.getEndTime().getTime() <= dayMiddle.getTime() && selfHoliday.getEndTime().getTime() > dayWorkStart.getTime()
						){
							//上午休假
							logger.debug( debugger, ">>>>>>>>>>"+detail.getEmpName()+"上午休假了");
							detail.setIsGetSelfHolidays(true);
							detail.setSelfHolidayDayTime("上午");
							detail.setGetSelfHolidayDays(0.5);
						}else if( selfHoliday.getStartTime().getTime() >= dayMiddle.getTime() && selfHoliday.getStartTime().getTime() <= dayWorkEnd.getTime()
						){
							//上午休假
							logger.debug( debugger, ">>>>>>>>>>"+detail.getEmpName()+"下午休假了");
							detail.setIsGetSelfHolidays( true );
							detail.setSelfHolidayDayTime("下午");
							detail.setGetSelfHolidayDays(0.5);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 分析一条打卡记录信息<br/>
		<b>迟到</b>：晚于最迟“迟到起算时间”，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>早退</b>：下班打卡时间早于“迟到起算时间”，并且工作当日时间不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>异常</b>：缺少签到退中的1条打卡数据 或者 上下班打卡时间都在最迟起算时间内，不满9个小时，（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）<br/>
		<b>缺勤</b>：当天没有打卡数据，并且当天是工作日（当天为非周末非节假日正常上班日，并且未请假，或者当天是调休的工作日）
	 * @param detail
	 * @param attendanceScheduleSetting
	 * @param dateOperation
	 * @throws Exception 
	 */
	private void analyseAttendanceDetail( AttendanceDetail detail, AttendanceScheduleSetting attendanceScheduleSetting, DateOperation dateOperation, Boolean debugger ) throws Exception {
		if( dateOperation == null ){
			dateOperation = new DateOperation();
		}
		if( detail == null ){
			throw new Exception("detail is null!" );
		}
		if( attendanceScheduleSetting == null ){
			throw new Exception("attendanceScheduleSetting is null, empName:" + detail.getEmpName() );
		}
		
		Date onDutyTime = null, offDutyTime = null;
		Date onWorkTime = null, offWorkTime = null;
		Date lateStartTime = null, leaveEarlyStartTime = null, absenceStartTime = null;
		Date morningEndTime = null;
		
		//先初始化当前打卡信息中的上下班时间要求，该要求是是根据员工所在组织排班信息获取到的
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
			logger.debug( debugger, ">>>>>>>>>>格式化[上午工作结束时间]morningEndTime=" +  detail.getRecordDateString() + " 12:00:00" );
			morningEndTime = dateOperation.getDateFromString( detail.getRecordDateString() + " 12:00:00"  );
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 系统进行时间转换时发生异常,morningEndTime=" + detail.getRecordDateString() + " 12:00:00" );
			morningEndTime = null;
			logger.warn( "系统进行时间转换时发生异常,morningEndTime=" + detail.getRecordDateString() + " 12:00:00" );
			logger.error(e);
		}
		
		
		if( onWorkTime != null && offWorkTime != null ){
			logger.debug( debugger, ">>>>>>>>>>上下班排班信息获取正常：onWorkTime=" +  onWorkTime + "， offWorkTime="+offWorkTime );
			//获取员工签到时间
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
			//获取员工签退时间
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
			if( onDutyTime == null && offDutyTime == null ){
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
					//上午签到过了，如果排班设置里已经配置过了缺勤起算时间，那么判断员工是否已经缺勤，如果未休假，则视为缺勤半天			
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
								detail.setAbsence(0.5);
								detail.setAbsentDayTime("上午");
							}
						}
						//缺勤直接扣半天出勤, 请假不算出勤，所以也只有半天
						detail.setAttendance( 0.5 );
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
					}
					long minutes = dateOperation.getMinutes( onDutyTime, morningEndTime );
					logger.debug( debugger, ">>>>>>>>>>上午工作时长, 从"+onDutyTime+"到"+morningEndTime+" ：minutes=" + minutes + "分钟。" );
					detail.setWorkTimeDuration( minutes );//记录上午的工作时长
				}else{
					logger.debug( debugger, ">>>>>>>>>>员工上午未打卡，异常状态......" );
					if( detail.getIsGetSelfHolidays() && ("上午".equalsIgnoreCase( detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))
					){
						logger.debug( debugger, ">>>>>>>>>>请幸运，请假不计考勤，不需要打卡，不算异常" );
						detail.setIsAbsent( false );
						//detail.setAttendance( 0.5 );
					}else{
						if( ( detail.getIsWeekend()&& !detail.getIsWorkday() ) //周末，并且未调休为工作日
								|| detail.getIsHoliday() //或者是节假日
							){
							logger.debug( debugger, ">>>>>>>>>>休息天，不算打卡异常，本来就不需要打卡" );
							detail.setAbnormalDutyDayTime("无");
							detail.setIsAbnormalDuty( false );
							//detail.setAttendance( 0 )
						}else{
							logger.debug( debugger, ">>>>>>>>>>呵呵，没请假，打卡异常。" );
							detail.setAbnormalDutyDayTime("上午");
							detail.setIsAbnormalDuty(true);
							//detail.setAttendance( 0.5 ); //上午打卡异常，不知道算不算出勤时间
						}
					}
					logger.debug( debugger, ">>>>>>>>>>上午工作时长, 未打卡：minutes= 0 分钟。" );
					detail.setWorkTimeDuration( 0L );//记录上午的工作时长
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
								minutes = dateOperation.getMinutes( offDutyTime, leaveEarlyStartTime );
								detail.setLeaveEarlierTimeDuration(minutes); //早退时间
								detail.setIsLeaveEarlier( true );
								//detail.setAttendance( detail.getAttendance() - 0.5 );//早退了，不知道算不算出勤
								logger.debug( debugger, ">>>>>>>>>>呵呵，没请假，早退计一次，全天工作时长："+detail.getWorkTimeDuration()+"分钟，早退时长：minutes=" + minutes );
							}
						}
					}
				}else{
					logger.debug( debugger, ">>>>>>>>>>员工下午未打卡，属于异常状态......" );
					//员工未签退，算缺勤了半天，出勤率: - 0.5
					if( detail.getIsGetSelfHolidays()  && ("下午".equalsIgnoreCase(detail.getSelfHolidayDayTime()) || "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() ))){
						logger.debug( debugger, ">>>>>>>>>>请幸运，请假不计考勤，不需要打卡，不算异常状态" );
						detail.setLeaveEarlierTimeDuration( 0L );
						detail.setIsLeaveEarlier( false );
						//detail.setAttendance( detail.getAttendance() - 0.5 );
					}else{
						if( ( detail.getIsWeekend() && !detail.getIsWorkday()) //周末，并且未调休为工作日
								|| detail.getIsHoliday() //或者是节假日
							){
							logger.debug( debugger, ">>>>>>>>>>休息天，不算异常" );
							detail.setAbnormalDutyDayTime("无");
							detail.setIsAbnormalDuty(false);
						}else{
							detail.setAbnormalDutyDayTime("下午");
							detail.setIsAbnormalDuty(true);
							logger.debug( debugger, ">>>>>>>>>>呵呵，没请假，未打卡，算异常状态。" );
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
	}
	
	private void saveAnalyseResultAndStatus( EntityManagerContainer emc, String id, int status, String description) throws Exception {
		AttendanceDetail detail = emc.find( id, AttendanceDetail.class );
		emc.beginTransaction( AttendanceDetail.class );
		detail.setRecordStatus( status );
		detail.setDescription( description );
		if( detail != null && detail.getEmpName() != null ){
			detail.setEmpName( detail.getEmpName().trim() );
		}
		emc.check( detail, CheckPersistType.all );
		emc.commit();
	}

}

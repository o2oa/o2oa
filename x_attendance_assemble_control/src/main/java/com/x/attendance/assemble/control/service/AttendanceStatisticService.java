package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceDetailStatisticFactory;
import com.x.attendance.assemble.control.factory.StatisticDepartmentForMonthFactory;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.attendance.entity.StatisticCompanyForDay;
import com.x.attendance.entity.StatisticCompanyForMonth;
import com.x.attendance.entity.StatisticDepartmentForDay;
import com.x.attendance.entity.StatisticDepartmentForMonth;
import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;

public class AttendanceStatisticService {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticService.class );
	
	/**
	 * 根据数据统计需求，进行员工每月考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param attendanceStatisticalCycle
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticEmployeeAttendanceForMonth( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			AttendanceStatisticalCycle attendanceStatisticalCycle, 
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null || attendanceStatisticalCycle == null ){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticEmployeeAttendanceForMonth( emc, attendanceStatisticRequireLog, attendanceStatisticalCycle, workDayConfigList, companyAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据数据统计需求，进行员工每月考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param attendanceStatisticalCycle
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticEmployeeAttendanceForMonth( EntityManagerContainer emc, 
			AttendanceStatisticRequireLog attendanceStatisticRequireLog, 
			AttendanceStatisticalCycle attendanceStatisticalCycle, 
			List<AttendanceWorkDayConfig> workDayConfigList, 
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) {
		
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<WrapIdentity> identities = null;
		List<String> query_employeeNames = new ArrayList<String>();
		List<String> departmentNames = null;
		WrapDepartment wrapDepartment = null;	
		StatisticPersonForMonth statisticPersonForMonth = null, statisticPersonForMonth_tmp = null;		
		Object workDayCountForMonth = 0, abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String cycleYear = null, cycleMonth = null, employeeName = null;
		Business business = null;
		
		cycleYear = attendanceStatisticRequireLog.getStatisticYear();
		cycleMonth = attendanceStatisticRequireLog.getStatisticMonth();
		employeeName = attendanceStatisticRequireLog.getStatisticKey();
		query_employeeNames.add( employeeName );
		
		try{
			business = new Business( emc );
		}catch(Exception e){
			logger.warn("系统在根据统计周期获取周期内应出勤工作日数量时发生异常！" );
			logger.error(e);
		}
		try{
			workDayCountForMonth = business.getAttendanceWorkDayConfigFactory().getWorkDaysCountForMonth( attendanceStatisticalCycle.getCycleStartDate(), attendanceStatisticalCycle.getCycleEndDate(), workDayConfigList );
		}catch(Exception e){
			logger.warn("系统在根据统计周期获取周期内应出勤工作日数量时发生异常！" );
			logger.error(e);
		}
		try{
			statisticPersonForMonth = new StatisticPersonForMonth();
			statisticPersonForMonth.setEmployeeName( employeeName );
			statisticPersonForMonth.setStatisticYear( cycleYear );
			statisticPersonForMonth.setStatisticMonth( cycleMonth );
			
			departmentNames = business.getAttendanceDetailFactory().distinctDetailsDepartmentNamesByCycleYearAndMonth( cycleYear, cycleMonth, employeeName );
			
			if( departmentNames != null && departmentNames.size() > 0){
				//logger.debug( "从打卡数据中查询到当月所在的部门是:");
				statisticPersonForMonth.setOrganizationName( departmentNames.get( 0 ) );
				wrapDepartment = business.organization().department().getWithName( departmentNames.get( 0 ) );
				if( wrapDepartment != null ){
					statisticPersonForMonth.setCompanyName( wrapDepartment.getCompany());
				}else{
					logger.warn( "根据部门["+departmentNames.get( 0 )+"]未查询到部门信息。" );
				}
			}else{
				//根据员工姓名，查询部门和公司名称
				identities = business.organization().identity().listWithPerson( employeeName );
				if( identities != null && identities.size() > 0){
					wrapDepartment = business.organization().department().getWithIdentity( identities.get(0).getName() );
					if( wrapDepartment != null ){
						statisticPersonForMonth.setOrganizationName( wrapDepartment.getName() );
						statisticPersonForMonth.setCompanyName( wrapDepartment.getCompany());
					}else{
						logger.warn( "根据身份["+identities.get(0).getName()+"]未查询到部门信息。" );
					}
				}else{
					logger.warn("系统中未查询到员工的身份，请管理员核实是否有为员工配置部门信息！");
				}	
			}
							
			//    1.2.1 应出勤天数
			if( workDayCountForMonth == null ){ workDayCountForMonth = 0L;}
			statisticPersonForMonth.setWorkDayCount( Double.parseDouble( workDayCountForMonth + "") );
			//    1.2.3 异常打卡次数
			abNormalDutyCount = business.getAttendanceDetailStatisticFactory().countAbNormalDutyByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticPersonForMonth.setAbNormalDutyCount((long)abNormalDutyCount);
			//    1.2.4 工时不足次数
			lackOfTimeCount = business.getAttendanceDetailStatisticFactory().countLackOfTimeByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticPersonForMonth.setLackOfTimeCount((long)lackOfTimeCount);
			//    1.2.5 签到次数
			onDutyTimes = business.getAttendanceDetailStatisticFactory().countOnDutyByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticPersonForMonth.setOnDutyTimes((long)onDutyTimes);
			//    1.2.6 签退次数
			offDutyTimes = business.getAttendanceDetailStatisticFactory().countOffDutyByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticPersonForMonth.setOffDutyTimes((long)offDutyTimes);
			//    1.2.7 迟到次数
			lateTimes = business.getAttendanceDetailStatisticFactory().countLateByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticPersonForMonth.setLateTimes((long)lateTimes);
			//    1.2.8 缺勤天数
			absenceDayCount = business.getAttendanceDetailStatisticFactory().sumAbsenceDaysByEmployeeYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticPersonForMonth.setAbsenceDayCount((double)absenceDayCount);
			//    1.2.9 早退次数
			leaveEarlyTimes = business.getAttendanceDetailStatisticFactory().countLeaveEarlierByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticPersonForMonth.setLeaveEarlyTimes((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			onSelfHolidayCount = business.getAttendanceDetailStatisticFactory().sumOnSelfHolidayDaysByEmployeeYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticPersonForMonth.setOnSelfHolidayCount((double)onSelfHolidayCount);
			
			//    1.2.2 实际出勤天数
			double onworkday = Double.parseDouble( workDayCountForMonth+"") - (double)absenceDayCount - (double)onSelfHolidayCount;
			if( onworkday < 0 ){
				onworkday = 0.0;
			}
			statisticPersonForMonth.setOnDutyDayCount( onworkday );
			
			//查询该员工该年份月份的统计是否存在，如果存在则删除
			statisticPersonForMonth_tmp = business.getStatisticPersonForMonthFactory().get( employeeName, cycleYear, cycleMonth );
			
			emc.beginTransaction(StatisticPersonForMonth.class);
			if( statisticPersonForMonth_tmp != null ){
				emc.remove( statisticPersonForMonth_tmp );
			}
			emc.persist( statisticPersonForMonth );
			emc.commit();
			
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
		}catch(Exception e){
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "ERROR", e.getMessage().length()>1000?e.getMessage().substring(0,700):e.getMessage() );
			logger.warn("系统在根据统计周期获取周期内统计数据时发生异常！" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据数据统计需求，进行部门每月考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticDepartmentAttendanceForMonth( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticDepartmentAttendanceForMonth( emc, attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行部门每月考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param attendanceStatisticalCycle
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticDepartmentAttendanceForMonth( EntityManagerContainer emc, 
			AttendanceStatisticRequireLog attendanceStatisticRequireLog, 
			List<AttendanceWorkDayConfig> workDayConfigList, 
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_departmentNames = new ArrayList<String>();
		WrapDepartment wrapDepartment  = null;
		StatisticDepartmentForMonth statisticDepartmentForMonth = null, statisticDepartmentForMonth_tmp = null;
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object workDayCountForMonth = 0.0, absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String cycleYear = null, cycleMonth = null, departmentName = null;
		Business business = null;
		

		cycleYear = attendanceStatisticRequireLog.getStatisticYear();
		cycleMonth = attendanceStatisticRequireLog.getStatisticMonth();
		departmentName = attendanceStatisticRequireLog.getStatisticKey();
		query_departmentNames.add( departmentName );
		
		try {
			business = new Business(emc);
			statisticDepartmentForMonth = new StatisticDepartmentForMonth();
			statisticDepartmentForMonth.setOrganizationName( departmentName );
			statisticDepartmentForMonth.setStatisticYear( cycleYear );
			statisticDepartmentForMonth.setStatisticMonth( cycleMonth );
			
			wrapDepartment = business.organization().department().getWithName(departmentName );
			
			if( wrapDepartment != null ){
				statisticDepartmentForMonth.setCompanyName( wrapDepartment.getCompany() );
			}else{
				logger.warn( "根据部门名称["+departmentName+"]未查询到部门信息。" );
			}
			//    1.2.1 应出勤天数
			workDayCountForMonth = business.getStatisticPersonForMonthFactory().sumAttendanceDayCountByDepartmentYearAndMonth(query_departmentNames, cycleYear, cycleMonth);
			double count = 0.0;
			if( workDayCountForMonth != null ){
				count = (double)workDayCountForMonth;
			}
			statisticDepartmentForMonth.setOnDutyEmployeeCount( count );
			//    1.2.3 异常打卡次数
			abNormalDutyCount = business.getStatisticPersonForMonthFactory().sumAbNormalDutyCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticDepartmentForMonth.setAbNormalDutyCount((long)abNormalDutyCount);
			//    1.2.4 工时不足次数
			lackOfTimeCount = business.getStatisticPersonForMonthFactory().sumLackOfTimeCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticDepartmentForMonth.setLackOfTimeCount((long)lackOfTimeCount);
			//    1.2.5 签到次数
			onDutyTimes = business.getStatisticPersonForMonthFactory().sumOnDutyCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticDepartmentForMonth.setOnDutyCount( (long)onDutyTimes);
			//    1.2.6 签退次数
			offDutyTimes = business.getStatisticPersonForMonthFactory().sumOffDutyCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticDepartmentForMonth.setOffDutyCount((long)offDutyTimes);
			//    1.2.7 迟到次数
			lateTimes = business.getStatisticPersonForMonthFactory().sumLateCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticDepartmentForMonth.setLateCount((long)lateTimes);
			//    1.2.8 缺勤天数
			absenceDayCount = business.getStatisticPersonForMonthFactory().sumAbsenceDayCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticDepartmentForMonth.setAbsenceDayCount((double)absenceDayCount);
			//    1.2.9 早退次数
			leaveEarlyTimes = business.getStatisticPersonForMonthFactory().sumLeaveEarlyCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticDepartmentForMonth.setLeaveEarlyCount((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			onSelfHolidayCount = business.getStatisticPersonForMonthFactory().sumOnSelfHolidayCountByDepartmentYearAndMonth( query_departmentNames, cycleYear, cycleMonth);
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticDepartmentForMonth.setOnSelfHolidayCount((double)onSelfHolidayCount);
			
			//查询该部门该年份月份的统计是否存在，如果存在则删除
			ids = business.getStatisticDepartmentForMonthFactory().listByDepartmentYearAndMonth( departmentName, cycleYear, cycleMonth );
			emc.beginTransaction(StatisticDepartmentForMonth.class);
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					statisticDepartmentForMonth_tmp = emc.find(id, StatisticDepartmentForMonth.class);
					emc.remove( statisticDepartmentForMonth_tmp );
				}
			}
			emc.persist( statisticDepartmentForMonth );
			emc.commit();
			
			if( attendanceStatisticRequireLog != null ){
				updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
			}
		}catch(Exception e){
			logger.warn("系统在根据统计周期获取周期内部门统计数据时发生异常！" );
			logger.error(e);
			if( attendanceStatisticRequireLog != null ){
				String id = attendanceStatisticRequireLog.getId();
				String message = e.getMessage().length()>700?e.getMessage().substring(0,700):e.getMessage();
				updateAttendanceStatisticRequireLog( id, "ERROR", message );
			}
		}
	}
	
	/**
	 * 根据数据统计需求，进行公司每月考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticCompanyAttendanceForMonth( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticCompanyAttendanceForMonth( emc, attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行公司每月考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticCompanyAttendanceForMonth( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_companyNames = null;
		StatisticCompanyForMonth statisticCompanyForMonth = null, statisticCompanyForMonth_tmp = null;
		StatisticDepartmentForMonthFactory statisticDepartmentForMonthFactory = null;
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object workDayCountForMonth = 0.0, absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String cycleYear = null, cycleMonth = null, companyName = null;
		Business business = null;
		
		cycleYear = attendanceStatisticRequireLog.getStatisticYear();
		cycleMonth = attendanceStatisticRequireLog.getStatisticMonth();
		companyName = attendanceStatisticRequireLog.getStatisticKey();
		query_companyNames = new ArrayList<String>();
		query_companyNames.add( companyName );
		
		try{
			business = new Business(emc);
			statisticDepartmentForMonthFactory = business.getStatisticDepartmentForMonthFactory();
			statisticCompanyForMonth = new StatisticCompanyForMonth();
			statisticCompanyForMonth.setStatisticYear(cycleYear);
			statisticCompanyForMonth.setStatisticMonth(cycleMonth);
			statisticCompanyForMonth.setCompanyName(companyName);
			if( statisticDepartmentForMonthFactory == null ){
				logger.warn(" statisticDepartmentForMonthFactory  is null !!!");
			}
			
			// 1.2.1 出勤天数
			workDayCountForMonth = statisticDepartmentForMonthFactory.sumAttendanceDayCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			double count = 0.0;
			if( workDayCountForMonth != null ){
				count = (double)workDayCountForMonth;
			}
			statisticCompanyForMonth.setOnDutyEmployeeCount( count );
			// 1.2.3 异常打卡次数	
			abNormalDutyCount = statisticDepartmentForMonthFactory.sumAbNormalDutyCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticCompanyForMonth.setAbNormalDutyCount((long)abNormalDutyCount);
			// 1.2.4 工时不足次数
			lackOfTimeCount = statisticDepartmentForMonthFactory.sumLackOfTimeCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticCompanyForMonth.setLackOfTimeCount((long)lackOfTimeCount);
			// 1.2.5 签到次数
			onDutyTimes = statisticDepartmentForMonthFactory.sumOnDutyCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticCompanyForMonth.setOnDutyCount( (long)onDutyTimes);
			// 1.2.6 签退次数
			offDutyTimes = statisticDepartmentForMonthFactory.sumOffDutyCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticCompanyForMonth.setOffDutyCount((long)offDutyTimes);
			// 1.2.7 迟到次数
			lateTimes = statisticDepartmentForMonthFactory.sumLateCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticCompanyForMonth.setLateCount((long)lateTimes);
			// 1.2.8 缺勤天数
			absenceDayCount = statisticDepartmentForMonthFactory.sumAbsenceDayCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticCompanyForMonth.setAbsenceDayCount((double)absenceDayCount);
			// 1.2.9 早退次数
			leaveEarlyTimes = statisticDepartmentForMonthFactory.sumLeaveEarlyCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticCompanyForMonth.setLeaveEarlyCount((long)leaveEarlyTimes);
			// 1.2.10 休假天数
			onSelfHolidayCount = statisticDepartmentForMonthFactory.sumOnSelfHolidayCountByCompanyNamesYearAndMonth( query_companyNames, cycleYear, cycleMonth);
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticCompanyForMonth.setOnSelfHolidayCount((double)onSelfHolidayCount);
			
			//查询该部门该年份月份的统计是否存在，如果存在则删除
			ids = business.getStatisticCompanyForMonthFactory().listByCompanyYearAndMonth( companyName, cycleYear, cycleMonth );
			emc.beginTransaction( StatisticCompanyForMonth.class );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					statisticCompanyForMonth_tmp = emc.find(id, StatisticCompanyForMonth.class);
					emc.remove( statisticCompanyForMonth_tmp );
				}
			}
			emc.persist( statisticCompanyForMonth );
			emc.commit();
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
		}catch(Exception e){
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "ERROR", e.getMessage().length()>1000?e.getMessage().substring(0,700):e.getMessage() );
			logger.warn("系统在根据统计周期获取周期内公司统计数据时发生异常！" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据数据统计需求，进行部门每日考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticDepartmentAttendanceForDay( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticDepartmentAttendanceForDay( emc, attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行部门每日考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticDepartmentAttendanceForDay( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_departmentNames = null;
		WrapDepartment wrapDepartment = null;
		StatisticDepartmentForDay statisticDepartmentForDay = null, statisticDepartmentForDay_tmp = null;
		AttendanceDetailStatisticFactory attendanceDetailStatisticFactory = null;		
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String statisticDate = null, departmentName = null;
		Business business = null;
		
		statisticDate = attendanceStatisticRequireLog.getStatisticDay();
		departmentName = attendanceStatisticRequireLog.getStatisticKey();
		query_departmentNames = new ArrayList<String>();
		query_departmentNames.add( departmentName );
		
		try{
			business = new Business(emc);
			attendanceDetailStatisticFactory = business.getAttendanceDetailStatisticFactory();
			statisticDepartmentForDay = new StatisticDepartmentForDay();
			statisticDepartmentForDay.setStatisticYear( "" );
			statisticDepartmentForDay.setStatisticMonth( "" );
			statisticDepartmentForDay.setStatisticDate(statisticDate);
			statisticDepartmentForDay.setOrganizationName(departmentName);
			wrapDepartment = business.organization().department().getWithName(departmentName );
			if( wrapDepartment != null ){
				statisticDepartmentForDay.setCompanyName( wrapDepartment.getCompany() );
			}else{
				logger.warn( "根据部门名称["+departmentName+"]未查询到部门信息。" );
			}
			//    4.2.1 应出勤天数
			//statisticDepartmentForDay.setWorkDayCount( workDayCountForMonth );
			//    4.2.3 异常打卡次数
			abNormalDutyCount = attendanceDetailStatisticFactory.countAbNormalDutyByDempartmentAndDate( query_departmentNames, statisticDate );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticDepartmentForDay.setAbNormalDutyCount((long)abNormalDutyCount);
			//    4.2.4 工时不足次数
			lackOfTimeCount = attendanceDetailStatisticFactory.countLackOfTimeByDempartmentAndDate( query_departmentNames, statisticDate );
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticDepartmentForDay.setLackOfTimeCount((long)lackOfTimeCount);
			//    4.2.5 签到次数
			onDutyTimes = attendanceDetailStatisticFactory.countOnDutyByDepartmentAndDate( query_departmentNames, statisticDate );
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticDepartmentForDay.setOnDutyCount((long)onDutyTimes);
			//    4.2.6 签退次数
			offDutyTimes = attendanceDetailStatisticFactory.countOffDutyByDepartmentAndDate( query_departmentNames, statisticDate );
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticDepartmentForDay.setOffDutyCount((long)offDutyTimes);
			//    4.2.7 迟到次数
			lateTimes = attendanceDetailStatisticFactory.countLateByDempartmentAndDate( query_departmentNames, statisticDate );
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticDepartmentForDay.setLateCount((long)lateTimes);
			//    4.2.8 缺勤天数
			absenceDayCount = attendanceDetailStatisticFactory.sumAbsenceDaysByDepartmentAndDate( query_departmentNames, statisticDate );
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticDepartmentForDay.setAbsenceDayCount((double)absenceDayCount);
			//    4.2.9 早退次数
			leaveEarlyTimes = attendanceDetailStatisticFactory.countLeaveEarlierByDempartmentAndDate( query_departmentNames, statisticDate );
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticDepartmentForDay.setLeaveEarlyCount((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			onSelfHolidayCount = attendanceDetailStatisticFactory.sumOnSelfHolidayDaysByDepartmentAndDate( query_departmentNames, statisticDate );
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticDepartmentForDay.setOnSelfHolidayEmployeeCount((double)onSelfHolidayCount);
			
			//    4.2.2 实际出勤天数
			//statisticDepartmentForDay.setOnDutyDayCount(workDayCountForMonth-absenceDayCount-onSelfHolidayCount);
			
			//查询该部门当天的统计是否存在，如果存在则删除
			ids = business.getStatisticDepartmentForDayFactory().listByDepartmentDayDate( departmentName, statisticDate );
			emc.beginTransaction( StatisticDepartmentForDay.class );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					//logger.debug("删除已存在的数据，id=" + id);
					statisticDepartmentForDay_tmp = emc.find(id, StatisticDepartmentForDay.class);
					emc.remove( statisticDepartmentForDay_tmp );
				}
			}else{
				//logger.debug("没有已存在的数据。" );
			}
			emc.persist( statisticDepartmentForDay );
			emc.commit();
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
		}catch(Exception e){
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "ERROR", e.getMessage().length()>1000?e.getMessage().substring(0,700):e.getMessage() );
			logger.warn("系统在根据统计周期获取周期内统计数据时发生异常！" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据数据统计需求，进行公司每日考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticCompanyAttendanceForDay( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticCompanyAttendanceForDay( emc, attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行公司每日考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param companyAttendanceStatisticalCycleMap
	 */
	public void statisticCompanyAttendanceForDay( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_companyNames = null;	
		StatisticCompanyForDay statisticCompanyForDay = null, statisticCompanyForDay_tmp = null;
		AttendanceDetailStatisticFactory attendanceDetailStatisticFactory = null;		
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String statisticDate = null, companyName = null;
		Business business = null;
		statisticDate = attendanceStatisticRequireLog.getStatisticDay();
		companyName = attendanceStatisticRequireLog.getStatisticKey();
		query_companyNames = new ArrayList<String>();
		query_companyNames.add( companyName );
		
		try{
			business = new Business(emc);
			attendanceDetailStatisticFactory = business.getAttendanceDetailStatisticFactory();
			statisticCompanyForDay = new StatisticCompanyForDay();
			statisticCompanyForDay.setStatisticYear( "" );
			statisticCompanyForDay.setStatisticMonth( "" );
			statisticCompanyForDay.setStatisticDate(statisticDate);
			statisticCompanyForDay.setCompanyName(companyName);
			//    4.2.1 应出勤天数
			//statisticCompanyForDay.setWorkDayCount( workDayCountForMonth );
			//    4.2.3 异常打卡次数
			abNormalDutyCount = attendanceDetailStatisticFactory.countAbNormalDutyByCompanyAndDate( query_companyNames, statisticDate );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticCompanyForDay.setAbNormalDutyCount((long)abNormalDutyCount);
			//    4.2.4 工时不足次数
			lackOfTimeCount = attendanceDetailStatisticFactory.countLackOfTimeByCompanyAndDate( query_companyNames, statisticDate );
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticCompanyForDay.setLackOfTimeCount((long)lackOfTimeCount);
			//    4.2.5 签到次数
			onDutyTimes = attendanceDetailStatisticFactory.countOnDutyByCompanyAndDate( query_companyNames, statisticDate );
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticCompanyForDay.setOnDutyCount((long)onDutyTimes);
			//    4.2.6 签退次数
			offDutyTimes = attendanceDetailStatisticFactory.countOffDutyByCompanyAndDate( query_companyNames, statisticDate );
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticCompanyForDay.setOffDutyCount((long)offDutyTimes);
			//    4.2.7 迟到次数
			lateTimes = attendanceDetailStatisticFactory.countLateByCompanyAndDate( query_companyNames, statisticDate );
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticCompanyForDay.setLateCount((long)lateTimes);
			//    4.2.8 缺勤天数
			absenceDayCount = attendanceDetailStatisticFactory.sumAbsenceDaysByCompanyAndDate( query_companyNames, statisticDate );
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticCompanyForDay.setAbsenceDayCount((double)absenceDayCount);
			//    4.2.9 早退次数
			leaveEarlyTimes = attendanceDetailStatisticFactory.countLeaveEarlierByCompanyAndDate( query_companyNames, statisticDate );
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticCompanyForDay.setLeaveEarlyCount((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			onSelfHolidayCount = attendanceDetailStatisticFactory.sumOnSelfHolidayDaysByCompanyAndDate( query_companyNames, statisticDate );
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticCompanyForDay.setOnSelfHolidayEmployeeCount((double)onSelfHolidayCount);
			
			//    4.2.2 实际出勤天数
			//statisticDepartmentForDay.setOnDutyDayCount(workDayCountForMonth-absenceDayCount-onSelfHolidayCount);
			
			//查询该公司当天的统计是否存在，如果存在则删除
			ids = business.getStatisticCompanyForDayFactory().listByCompanyRecordDateString( companyName, statisticDate );
			emc.beginTransaction( StatisticCompanyForDay.class );
			if( ids != null && ids.size() > 0 ){
				for( String id : ids ){
					statisticCompanyForDay_tmp = emc.find(id, StatisticCompanyForDay.class);
					emc.remove( statisticCompanyForDay_tmp );
				}
			}
			emc.persist( statisticCompanyForDay );
			emc.commit();
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
		}catch(Exception e){
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "ERROR", e.getMessage().length()>1000?e.getMessage().substring(0,700):e.getMessage() );
			logger.warn("系统在根据统计周期获取周期内统计数据时发生异常！" );
			logger.error(e);
		}
	}
	
	private void updateAttendanceStatisticRequireLog( String id, String status, String message) {
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			attendanceStatisticRequireLog = emc.find( id, AttendanceStatisticRequireLog.class );
			if( attendanceStatisticRequireLog != null ){
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				attendanceStatisticRequireLog.setProcessTime( new Date() );
				attendanceStatisticRequireLog.setProcessStatus( status );
				attendanceStatisticRequireLog.setDescription( message );
				emc.check( attendanceStatisticRequireLog, CheckPersistType.all);
				emc.commit();	
			}else{
				logger.warn( "系统未能根据ID查询到统计需求信息：{'id':'"+id+"'}" );
			}
		}catch(Exception e){
			logger.warn( "系统在更新统计需求信息状态时发生异常！" );
			logger.error(e);
		}
	}
	public List<String> listPersonForMonthByUserYearAndMonth(EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticPersonForMonthFactory().listByUserYearAndMonth( name, year, month );
	}
	public List<StatisticPersonForMonth> listPersonForMonth(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticPersonForMonthFactory().list( ids );
	}
	public List<String> listPersonForMonthByDepartmentYearAndMonth(EntityManagerContainer emc, List<String> departmentNameList,
			String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticPersonForMonthFactory().listByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	public List<String> listDepartmentForMonthByDepartmentYearAndMonth(EntityManagerContainer emc, List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().listByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	public List<StatisticDepartmentForMonth> listDepartmentForMonth(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().list( ids );
	}
	public List<String> listDepartmentForMonthByDepartmentYearAndMonth(EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().listByDepartmentYearAndMonth( name, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员缺勤人次总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumDepartmentForMonth_AbsenceDayCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumAbsenceDayCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员请假人次总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumDepartmentForMonth_OnSelfHolidayCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumOnSelfHolidayCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员迟到次数总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_LateCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumLateCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员早退人次总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_LeaveEarlyCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumLeaveEarlyCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员签到人次总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_OnDutyCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumOnDutyCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员签退人次总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_OffDutyCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumOffDutyCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员异常打卡次数总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_AbNormalDutyCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumAbNormalDutyCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员工时不足人次总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_LackOfTimeCount_ByDepartmentYearAndMonth(EntityManagerContainer emc,
			List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumLackOfTimeCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员出勤人天总和
	 * @param emc
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumDepartmentForMonth_AttendanceDayCount_ByDepartmentYearAndMonth(EntityManagerContainer emc, List<String> departmentNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForMonthFactory().sumAttendanceDayCountByDepartmentYearAndMonth( departmentNameList, year, month );
	}
	
	public List<String> listStatisticCompanyForMonth_ByCompanyYearAndMonth( EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticCompanyForMonthFactory().listByCompanyYearAndMonth( name, year, month );
	}
	public List<StatisticCompanyForMonth> listCompanyForMonth(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticCompanyForMonthFactory().list( ids );
	}
	public List<String> listStatisticDepartmentForDay_ByDepartmentDayYearAndMonth(EntityManagerContainer emc, List<String> departmentNames, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForDayFactory().listByDepartmentDayYearAndMonth( departmentNames, year, month );
	}
	public List<StatisticDepartmentForDay> listDepartmentForDay(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticDepartmentForDayFactory().list( ids );
	}
	public List<String> listStatisticCompanyForDay_ByNameYearAndMonth(EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticCompanyForDayFactory().listByNameYearAndMonth( name, year, month );
	}
	public List<StatisticCompanyForDay> listCompanyForDay(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticCompanyForDayFactory().list( ids );
	}
}

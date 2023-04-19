package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceDetailStatisticFactory;
import com.x.attendance.assemble.control.factory.StatisticUnitForMonthFactory;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.attendance.entity.StatisticTopUnitForDay;
import com.x.attendance.entity.StatisticTopUnitForMonth;
import com.x.attendance.entity.StatisticUnitForDay;
import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class AttendanceStatisticService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceStatisticService.class );
	private UserManagerService userManagerService = new UserManagerService();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	
	/**
	 * 根据数据统计需求，进行员工每月考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param attendanceStatisticalCycle
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticEmployeeAttendanceForMonth( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			AttendanceStatisticalCycle attendanceStatisticalCycle, 
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null || attendanceStatisticalCycle == null ){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticEmployeeAttendanceForMonth( emc, attendanceStatisticRequireLog, attendanceStatisticalCycle, workDayConfigList, topUnitAttendanceStatisticalCycleMap );
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
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticEmployeeAttendanceForMonth( EntityManagerContainer emc, 
			AttendanceStatisticRequireLog attendanceStatisticRequireLog, 
			AttendanceStatisticalCycle attendanceStatisticalCycle, 
			List<AttendanceWorkDayConfig> workDayConfigList, 
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap ) {
		
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> identities = null;
		List<String> query_employeeNames = new ArrayList<String>();
		List<String> unitNames = null;
		String unitName = null;
		String topUnitName = null;
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
			
			unitNames = business.getAttendanceDetailFactory().distinctDetailsUnitNamesByCycleYearAndMonth( cycleYear, cycleMonth, employeeName );
			
			if( unitNames != null && unitNames.size() > 0){
				topUnitName = userManagerService.getTopUnitNameWithUnitName( unitNames.get( 0 ) );
				statisticPersonForMonth.setUnitName( unitNames.get( 0 ) );
				if( topUnitName != null ){
					statisticPersonForMonth.setTopUnitName( topUnitName );
				}else{
					logger.warn( "根据组织["+unitNames.get( 0 )+"]未查询到顶层组织信息。" );
				}
			}else{
				//根据员工姓名，查询组织和顶层组织名称
				identities =  userManagerService.listIdentitiesWithPerson( employeeName );
				if( identities != null && identities.size() > 0){
					unitName = userManagerService.getUnitNameWithIdentity( identities.get(0) );
					topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
					if( topUnitName != null ){
						statisticPersonForMonth.setUnitName( unitName );
						statisticPersonForMonth.setTopUnitName( topUnitName );
					}else{
						logger.warn( "根据身份["+identities.get(0)+"]未查询到组织信息。" );
					}
				}else{
					logger.warn("系统中未查询到员工的身份，请管理员核实是否有为员工配置组织信息！");
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
			Long onworkday = business.getAttendanceDetailStatisticFactory().countDutyDaysByEmployeeCycleYearAndMonth(query_employeeNames, cycleYear, cycleMonth);
			if (onworkday == null) {
				onworkday = 0L;
			}
//			double onworkday = Double.parseDouble( workDayCountForMonth+"") - (double)absenceDayCount - (double)onSelfHolidayCount;
//			if( onworkday < 0 ){
//				onworkday = 0.0;
//			}
			statisticPersonForMonth.setOnDutyDayCount( Double.parseDouble(onworkday+"") );
			
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
	 * 根据数据统计需求，进行组织每月考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticUnitAttendanceForMonth( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticUnitAttendanceForMonth( emc, attendanceStatisticRequireLog, workDayConfigList, topUnitAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据数据统计需求，进行组织每月考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticUnitAttendanceForMonth( EntityManagerContainer emc, 
			AttendanceStatisticRequireLog attendanceStatisticRequireLog, 
			List<AttendanceWorkDayConfig> workDayConfigList, 
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_unitNames = new ArrayList<String>();
		String unitName  = null;
		String topUnitName  = null;
		StatisticUnitForMonth statisticUnitForMonth = null, statisticUnitForMonth_tmp = null;
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object workDayCountForMonth = 0.0, absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String cycleYear = null, cycleMonth = null;
		Business business = null;
		List<String> unUnitNameList = new ArrayList<String>();
		List<String> personNameList = new ArrayList<String>();

		cycleYear = attendanceStatisticRequireLog.getStatisticYear();
		cycleMonth = attendanceStatisticRequireLog.getStatisticMonth();
		unitName = attendanceStatisticRequireLog.getStatisticKey();
		query_unitNames.add( unitName );
		
		try {
			unUnitNameList = getUnUnitNameList();
			personNameList = getUnPersonNameList();
			business = new Business(emc);
			statisticUnitForMonth = new StatisticUnitForMonth();
			statisticUnitForMonth.setUnitName( unitName );
			statisticUnitForMonth.setStatisticYear( cycleYear );
			statisticUnitForMonth.setStatisticMonth( cycleMonth );
			
			topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
			if( topUnitName != null ){
				statisticUnitForMonth.setTopUnitName( topUnitName );
			}else{
				logger.warn( "根据组织名称["+unitName+"]未查询到组织信息。" );
			}
			//    1.2.1 应出勤天数
			//workDayCountForMonth = business.getStatisticPersonForMonthFactory().sumAttendanceDayCountByUnitYearAndMonth(query_unitNames, cycleYear, cycleMonth);
			workDayCountForMonth = business.getStatisticPersonForMonthFactory().sumAttendanceDayCountByUnitYearAndMonthUn(query_unitNames,unUnitNameList,personNameList, cycleYear, cycleMonth);
			double count = 0.0;
			if( workDayCountForMonth != null ){
				count = (double)workDayCountForMonth;
			}
			statisticUnitForMonth.setOnDutyEmployeeCount( count );
			//    1.2.3 异常打卡次数
			//abNormalDutyCount = business.getStatisticPersonForMonthFactory().sumAbNormalDutyCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth );
			abNormalDutyCount = business.getStatisticPersonForMonthFactory().sumAbNormalDutyCountByUnitYearAndMonthUn( query_unitNames,unUnitNameList,personNameList, cycleYear, cycleMonth );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticUnitForMonth.setAbNormalDutyCount((long)abNormalDutyCount);
			//    1.2.4 工时不足次数
			//lackOfTimeCount = business.getStatisticPersonForMonthFactory().sumLackOfTimeCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			lackOfTimeCount = business.getStatisticPersonForMonthFactory().sumLackOfTimeCountByUnitYearAndMonthUn( query_unitNames,unUnitNameList,personNameList, cycleYear, cycleMonth);
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticUnitForMonth.setLackOfTimeCount((long)lackOfTimeCount);
			//    1.2.5 签到次数
			//onDutyTimes = business.getStatisticPersonForMonthFactory().sumOnDutyCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			onDutyTimes = business.getStatisticPersonForMonthFactory().sumOnDutyCountByUnitYearAndMonthUn( query_unitNames,unUnitNameList,personNameList, cycleYear, cycleMonth);
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticUnitForMonth.setOnDutyCount( (long)onDutyTimes);
			//    1.2.6 签退次数
			//offDutyTimes = business.getStatisticPersonForMonthFactory().sumOffDutyCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			offDutyTimes = business.getStatisticPersonForMonthFactory().sumOffDutyCountByUnitYearAndMonthUn( query_unitNames, unUnitNameList,personNameList,cycleYear, cycleMonth);
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticUnitForMonth.setOffDutyCount((long)offDutyTimes);
			//    1.2.7 迟到次数
			//lateTimes = business.getStatisticPersonForMonthFactory().sumLateCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			lateTimes = business.getStatisticPersonForMonthFactory().sumLateCountByUnitYearAndMonthUn( query_unitNames, unUnitNameList,personNameList,cycleYear, cycleMonth);
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticUnitForMonth.setLateCount((long)lateTimes);
			//    1.2.8 缺勤天数
			//absenceDayCount = business.getStatisticPersonForMonthFactory().sumAbsenceDayCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			absenceDayCount = business.getStatisticPersonForMonthFactory().sumAbsenceDayCountByUnitYearAndMonthUn( query_unitNames, unUnitNameList,personNameList,cycleYear, cycleMonth);
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticUnitForMonth.setAbsenceDayCount((double)absenceDayCount);
			//    1.2.9 早退次数
			//leaveEarlyTimes = business.getStatisticPersonForMonthFactory().sumLeaveEarlyCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			leaveEarlyTimes = business.getStatisticPersonForMonthFactory().sumLeaveEarlyCountByUnitYearAndMonthUn( query_unitNames, unUnitNameList,personNameList,cycleYear, cycleMonth);
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticUnitForMonth.setLeaveEarlyCount((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			//onSelfHolidayCount = business.getStatisticPersonForMonthFactory().sumOnSelfHolidayCountByUnitYearAndMonth( query_unitNames, cycleYear, cycleMonth);
			onSelfHolidayCount = business.getStatisticPersonForMonthFactory().sumOnSelfHolidayCountByUnitYearAndMonthUn( query_unitNames,unUnitNameList,personNameList, cycleYear, cycleMonth);
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticUnitForMonth.setOnSelfHolidayCount((double)onSelfHolidayCount);
			
			//查询该组织该年份月份的统计是否存在，如果存在则删除
			ids = business.getStatisticUnitForMonthFactory().listByUnitYearAndMonth( unitName, cycleYear, cycleMonth );
			emc.beginTransaction(StatisticUnitForMonth.class);
			if( ListTools.isNotEmpty( ids ) ){
				for( String id : ids ){
					statisticUnitForMonth_tmp = emc.find( id, StatisticUnitForMonth.class );
					emc.remove( statisticUnitForMonth_tmp );
				}
			}
			emc.persist( statisticUnitForMonth );
			emc.commit();
			
			if( attendanceStatisticRequireLog != null ){
				updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
			}
		}catch(Exception e){
			logger.warn("系统在根据统计周期获取周期内组织统计数据时发生异常！" );
			logger.error(e);
			if( attendanceStatisticRequireLog != null ){
				String id = attendanceStatisticRequireLog.getId();
				String message = e.getMessage().length()>700?e.getMessage().substring(0,700):e.getMessage();
				updateAttendanceStatisticRequireLog( id, "ERROR", message );
			}
		}
	}
	
	/**
	 * 根据数据统计需求，进行顶层组织每月考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticTopUnitAttendanceForMonth( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticTopUnitAttendanceForMonth( emc, attendanceStatisticRequireLog, workDayConfigList, topUnitAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行顶层组织每月考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticTopUnitAttendanceForMonth( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_topUnitNames = null;
		StatisticTopUnitForMonth statisticTopUnitForMonth = null, statisticTopUnitForMonth_tmp = null;
		StatisticUnitForMonthFactory statisticUnitForMonthFactory = null;
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object workDayCountForMonth = 0.0, absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String cycleYear = null, cycleMonth = null, topUnitName = null;
		Business business = null;
		
		cycleYear = attendanceStatisticRequireLog.getStatisticYear();
		cycleMonth = attendanceStatisticRequireLog.getStatisticMonth();
		topUnitName = attendanceStatisticRequireLog.getStatisticKey();
		query_topUnitNames = new ArrayList<String>();
		query_topUnitNames.add( topUnitName );
		
		try{
			business = new Business(emc);
			statisticUnitForMonthFactory = business.getStatisticUnitForMonthFactory();
			statisticTopUnitForMonth = new StatisticTopUnitForMonth();
			statisticTopUnitForMonth.setStatisticYear(cycleYear);
			statisticTopUnitForMonth.setStatisticMonth(cycleMonth);
			statisticTopUnitForMonth.setTopUnitName(topUnitName);
			if( statisticUnitForMonthFactory == null ){
				logger.warn(" statisticUnitForMonthFactory  is null !!!");
			}
			
			// 1.2.1 出勤天数
			workDayCountForMonth = statisticUnitForMonthFactory.sumAttendanceDayCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			double count = 0.0;
			if( workDayCountForMonth != null ){
				count = (double)workDayCountForMonth;
			}
			statisticTopUnitForMonth.setOnDutyEmployeeCount( count );
			// 1.2.3 异常打卡次数	
			abNormalDutyCount = statisticUnitForMonthFactory.sumAbNormalDutyCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticTopUnitForMonth.setAbNormalDutyCount((long)abNormalDutyCount);
			// 1.2.4 工时不足次数
			lackOfTimeCount = statisticUnitForMonthFactory.sumLackOfTimeCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticTopUnitForMonth.setLackOfTimeCount((long)lackOfTimeCount);
			// 1.2.5 签到次数
			onDutyTimes = statisticUnitForMonthFactory.sumOnDutyCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticTopUnitForMonth.setOnDutyCount( (long)onDutyTimes);
			// 1.2.6 签退次数
			offDutyTimes = statisticUnitForMonthFactory.sumOffDutyCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticTopUnitForMonth.setOffDutyCount((long)offDutyTimes);
			// 1.2.7 迟到次数
			lateTimes = statisticUnitForMonthFactory.sumLateCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticTopUnitForMonth.setLateCount((long)lateTimes);
			// 1.2.8 缺勤天数
			absenceDayCount = statisticUnitForMonthFactory.sumAbsenceDayCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticTopUnitForMonth.setAbsenceDayCount((double)absenceDayCount);
			// 1.2.9 早退次数
			leaveEarlyTimes = statisticUnitForMonthFactory.sumLeaveEarlyCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticTopUnitForMonth.setLeaveEarlyCount((long)leaveEarlyTimes);
			// 1.2.10 休假天数
			onSelfHolidayCount = statisticUnitForMonthFactory.sumOnSelfHolidayCountByTopUnitNamesYearAndMonth( query_topUnitNames, cycleYear, cycleMonth);
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticTopUnitForMonth.setOnSelfHolidayCount((double)onSelfHolidayCount);
			
			//查询该组织该年份月份的统计是否存在，如果存在则删除
			ids = business.getStatisticTopUnitForMonthFactory().listByTopUnitYearAndMonth( topUnitName, cycleYear, cycleMonth );
			emc.beginTransaction( StatisticTopUnitForMonth.class );
			if( ListTools.isNotEmpty( ids ) ){
				for( String id : ids ){
					statisticTopUnitForMonth_tmp = emc.find(id, StatisticTopUnitForMonth.class);
					emc.remove( statisticTopUnitForMonth_tmp );
				}
			}
			emc.persist( statisticTopUnitForMonth );
			emc.commit();
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
		}catch(Exception e){
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "ERROR", e.getMessage().length()>1000?e.getMessage().substring(0,700):e.getMessage() );
			logger.warn("系统在根据统计周期获取周期内顶层组织统计数据时发生异常！" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据数据统计需求，进行组织每日考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticUnitAttendanceForDay( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap,
			Boolean debugger ) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticUnitAttendanceForDay( emc, attendanceStatisticRequireLog, workDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行组织每日考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticUnitAttendanceForDay( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_unitNames = null;
		String unitName = null;
		String topUnitName = null;
		StatisticUnitForDay statisticUnitForDay = null, statisticUnitForDay_tmp = null;
		AttendanceDetailStatisticFactory attendanceDetailStatisticFactory = null;		
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String statisticDate = null;
		Business business = null;
		
		statisticDate = attendanceStatisticRequireLog.getStatisticDay();
		unitName = attendanceStatisticRequireLog.getStatisticKey();
		query_unitNames = new ArrayList<String>();
		query_unitNames.add( unitName );
		
		try{
			business = new Business(emc);
			attendanceDetailStatisticFactory = business.getAttendanceDetailStatisticFactory();
			statisticUnitForDay = new StatisticUnitForDay();
			statisticUnitForDay.setStatisticYear( "" );
			statisticUnitForDay.setStatisticMonth( "" );
			statisticUnitForDay.setStatisticDate(statisticDate);
			statisticUnitForDay.setUnitName(unitName);

			if( unitName != null ){
				topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
				statisticUnitForDay.setTopUnitName( topUnitName );
			}else{
				logger.warn( "根据组织名称["+unitName+"]未查询到组织信息。" );
			}
			//    4.2.1 应出勤天数
			//statisticUnitForDay.setWorkDayCount( workDayCountForMonth );
			//    4.2.3 异常打卡次数
			abNormalDutyCount = attendanceDetailStatisticFactory.countAbNormalDutyByUnitAndDate( query_unitNames, statisticDate );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticUnitForDay.setAbNormalDutyCount((long)abNormalDutyCount);
			//    4.2.4 工时不足次数
			lackOfTimeCount = attendanceDetailStatisticFactory.countLackOfTimeByUnitAndDate( query_unitNames, statisticDate );
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticUnitForDay.setLackOfTimeCount((long)lackOfTimeCount);
			//    4.2.5 签到次数
			onDutyTimes = attendanceDetailStatisticFactory.countOnDutyByUnitAndDate( query_unitNames, statisticDate );
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticUnitForDay.setOnDutyCount((long)onDutyTimes);
			//    4.2.6 签退次数
			offDutyTimes = attendanceDetailStatisticFactory.countOffDutyByUnitAndDate( query_unitNames, statisticDate );
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticUnitForDay.setOffDutyCount((long)offDutyTimes);
			//    4.2.7 迟到次数
			lateTimes = attendanceDetailStatisticFactory.countLateByUnitAndDate( query_unitNames, statisticDate );
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticUnitForDay.setLateCount((long)lateTimes);
			//    4.2.8 缺勤天数
			absenceDayCount = attendanceDetailStatisticFactory.sumAbsenceDaysByUnitAndDate( query_unitNames, statisticDate );
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticUnitForDay.setAbsenceDayCount((double)absenceDayCount);
			//    4.2.9 早退次数
			leaveEarlyTimes = attendanceDetailStatisticFactory.countLeaveEarlierByUnitAndDate( query_unitNames, statisticDate );
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticUnitForDay.setLeaveEarlyCount((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			onSelfHolidayCount = attendanceDetailStatisticFactory.sumOnSelfHolidayDaysByUnitAndDate( query_unitNames, statisticDate );
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticUnitForDay.setOnSelfHolidayEmployeeCount((double)onSelfHolidayCount);
			
			//    4.2.2 实际出勤天数
			//statisticUnitForDay.setOnDutyDayCount(workDayCountForMonth-absenceDayCount-onSelfHolidayCount);
			
			//查询该组织当天的统计是否存在，如果存在则删除
			ids = business.getStatisticUnitForDayFactory().listByUnitDayDate( unitName, statisticDate );
			emc.beginTransaction( StatisticUnitForDay.class );
			if( ListTools.isNotEmpty( ids ) ){
				for( String id : ids ){
					logger.debug( debugger, ">>>>>>>>>>删除已存在的数据，id=" + id);
					statisticUnitForDay_tmp = emc.find(id, StatisticUnitForDay.class);
					emc.remove( statisticUnitForDay_tmp );
				}
			}else{
				logger.debug( debugger, ">>>>>>>>>>没有已存在的数据。" );
			}
			emc.persist( statisticUnitForDay );
			emc.commit();
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "COMPLETED", "处理成功" );
		}catch(Exception e){
			updateAttendanceStatisticRequireLog( attendanceStatisticRequireLog.getId(), "ERROR", e.getMessage().length()>1000?e.getMessage().substring(0,700):e.getMessage() );
			logger.warn("系统在根据统计周期获取周期内统计数据时发生异常！" );
			logger.error(e);
		}
	}
	
	/**
	 * 根据数据统计需求，进行顶层组织每日考勤分析结果统计
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticTopUnitAttendanceForDay( AttendanceStatisticRequireLog attendanceStatisticRequireLog,
			List<AttendanceWorkDayConfig> workDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) throws Exception {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			statisticTopUnitAttendanceForDay( emc, attendanceStatisticRequireLog, workDayConfigList, topUnitAttendanceStatisticalCycleMap );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据数据统计需求，进行顶层组织每日考勤分析结果统计
	 * @param emc
	 * @param attendanceStatisticRequireLog
	 * @param workDayConfigList
	 * @param topUnitAttendanceStatisticalCycleMap
	 */
	public void statisticTopUnitAttendanceForDay( EntityManagerContainer emc, AttendanceStatisticRequireLog attendanceStatisticRequireLog, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap ) {
		if( attendanceStatisticRequireLog == null ){
			return;
		}
		List<String> ids = null;
		List<String> query_topUnitNames = null;	
		StatisticTopUnitForDay statisticTopUnitForDay = null, statisticTopUnitForDay_tmp = null;
		AttendanceDetailStatisticFactory attendanceDetailStatisticFactory = null;		
		Object abNormalDutyCount=0L, lackOfTimeCount=0L, onDutyTimes=0L, offDutyTimes=0L, lateTimes=0L, leaveEarlyTimes=0L;
		Object absenceDayCount=0.0, onSelfHolidayCount=0.0;
		String statisticDate = null, topUnitName = null;
		Business business = null;
		statisticDate = attendanceStatisticRequireLog.getStatisticDay();
		topUnitName = attendanceStatisticRequireLog.getStatisticKey();
		query_topUnitNames = new ArrayList<String>();
		query_topUnitNames.add( topUnitName );
		
		try{
			business = new Business(emc);
			attendanceDetailStatisticFactory = business.getAttendanceDetailStatisticFactory();
			statisticTopUnitForDay = new StatisticTopUnitForDay();
			statisticTopUnitForDay.setStatisticYear( "" );
			statisticTopUnitForDay.setStatisticMonth( "" );
			statisticTopUnitForDay.setStatisticDate(statisticDate);
			statisticTopUnitForDay.setTopUnitName(topUnitName);
			//    4.2.1 应出勤天数
			//statisticTopUnitForDay.setWorkDayCount( workDayCountForMonth );
			//    4.2.3 异常打卡次数
			abNormalDutyCount = attendanceDetailStatisticFactory.countAbNormalDutyByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( abNormalDutyCount == null ){ abNormalDutyCount = 0L;}
			statisticTopUnitForDay.setAbNormalDutyCount((long)abNormalDutyCount);
			//    4.2.4 工时不足次数
			lackOfTimeCount = attendanceDetailStatisticFactory.countLackOfTimeByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( lackOfTimeCount == null ){ lackOfTimeCount = 0L;}
			statisticTopUnitForDay.setLackOfTimeCount((long)lackOfTimeCount);
			//    4.2.5 签到次数
			onDutyTimes = attendanceDetailStatisticFactory.countOnDutyByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( onDutyTimes == null ){ onDutyTimes = 0L;}
			statisticTopUnitForDay.setOnDutyCount((long)onDutyTimes);
			//    4.2.6 签退次数
			offDutyTimes = attendanceDetailStatisticFactory.countOffDutyByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( offDutyTimes == null ){ offDutyTimes = 0L;}
			statisticTopUnitForDay.setOffDutyCount((long)offDutyTimes);
			//    4.2.7 迟到次数
			lateTimes = attendanceDetailStatisticFactory.countLateByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( lateTimes == null ){ lateTimes = 0L;}
			statisticTopUnitForDay.setLateCount((long)lateTimes);
			//    4.2.8 缺勤天数
			absenceDayCount = attendanceDetailStatisticFactory.sumAbsenceDaysByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( absenceDayCount == null ){ absenceDayCount = 0.0;}
			statisticTopUnitForDay.setAbsenceDayCount((double)absenceDayCount);
			//    4.2.9 早退次数
			leaveEarlyTimes = attendanceDetailStatisticFactory.countLeaveEarlierByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( leaveEarlyTimes == null ){ leaveEarlyTimes = 0L;}
			statisticTopUnitForDay.setLeaveEarlyCount((long)leaveEarlyTimes);
			//    1.2.10 休假天数
			onSelfHolidayCount = attendanceDetailStatisticFactory.sumOnSelfHolidayDaysByTopUnitAndDate( query_topUnitNames, statisticDate );
			if( onSelfHolidayCount == null ){ onSelfHolidayCount = 0.0;}
			statisticTopUnitForDay.setOnSelfHolidayEmployeeCount((double)onSelfHolidayCount);
			
			//    4.2.2 实际出勤天数
			//statisticUnitForDay.setOnDutyDayCount(workDayCountForMonth-absenceDayCount-onSelfHolidayCount);
			
			//查询该顶层组织当天的统计是否存在，如果存在则删除
			ids = business.getStatisticTopUnitForDayFactory().listByTopUnitRecordDateString( topUnitName, statisticDate );
			emc.beginTransaction( StatisticTopUnitForDay.class );
			if( ListTools.isNotEmpty( ids ) ){
				for( String id : ids ){
					statisticTopUnitForDay_tmp = emc.find(id, StatisticTopUnitForDay.class);
					emc.remove( statisticTopUnitForDay_tmp );
				}
			}
			emc.persist( statisticTopUnitForDay );
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
	public List<String> listPersonForMonthByUnitYearAndMonth(EntityManagerContainer emc, List<String> unitNameList,
			String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticPersonForMonthFactory().listByUnitYearAndMonth( unitNameList, year, month );
	}
	//排除不需要的组织和人员
	public List<String> listPersonForMonthByUnitYearMonthAndUn(EntityManagerContainer emc, List<String> unitNameList,List<String> unUnitNameList,List<String> personNameList,
			String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticPersonForMonthFactory().listPersonForMonthByUnitYearMonthAndUn( unitNameList,unUnitNameList,personNameList, year, month );
	}
	
	public List<String> listUnitForMonthByUnitYearAndMonth(EntityManagerContainer emc, List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().listByUnitYearAndMonth( unitNameList, year, month );
	}
	public List<StatisticUnitForMonth> listUnitForMonth(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().list( ids );
	}
	public List<String> listUnitForMonthByUnitYearAndMonth(EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().listByUnitYearAndMonth( name, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员缺勤人次总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_AbsenceDayCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumAbsenceDayCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员请假人次总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_OnSelfHolidayCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumOnSelfHolidayCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员迟到次数总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LateCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumLateCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员早退人次总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LeaveEarlyCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumLeaveEarlyCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员签到人次总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_OnDutyCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumOnDutyCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员签退人次总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_OffDutyCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumOffDutyCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员异常打卡次数总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_AbNormalDutyCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumAbNormalDutyCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员工时不足人次总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LackOfTimeCount_ByUnitYearAndMonth(EntityManagerContainer emc,
			List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumLackOfTimeCountByUnitYearAndMonth( unitNameList, year, month );
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员出勤人天总和
	 * @param emc
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_AttendanceDayCount_ByUnitYearAndMonth(EntityManagerContainer emc, List<String> unitNameList, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForMonthFactory().sumAttendanceDayCountByUnitYearAndMonth( unitNameList, year, month );
	}
	
	public List<String> listStatisticTopUnitForMonth_ByTopUnitYearAndMonth( EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticTopUnitForMonthFactory().listByTopUnitYearAndMonth( name, year, month );
	}
	public List<StatisticTopUnitForMonth> listTopUnitForMonth(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticTopUnitForMonthFactory().list( ids );
	}
	public List<String> listStatisticUnitForDay_ByUnitDayYearAndMonth(EntityManagerContainer emc, List<String> unitNames, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForDayFactory().listByUnitDayYearAndMonth( unitNames, year, month );
	}
	public List<StatisticUnitForDay> listUnitForDay(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticUnitForDayFactory().list( ids );
	}
	public List<String> listStatisticTopUnitForDay_ByNameYearAndMonth(EntityManagerContainer emc, String name, String year, String month) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticTopUnitForDayFactory().listByNameYearAndMonth( name, year, month );
	}
	public List<StatisticTopUnitForDay> listTopUnitForDay(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.getStatisticTopUnitForDayFactory().list( ids );
	}
	
	/**
	 * 获取不需要考勤的组织
	 * @return
	 * @throws Exception 
	 */
	protected  List<String> getUnUnitNameList() throws Exception {
		List<String> unUnitNameList = new ArrayList<String>();

		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String unitName = attendanceEmployeeConfig.getUnitName();
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isEmpty(employeeName) && StringUtils.isNotEmpty(unitName)){
					unUnitNameList.add(unitName);
					List<String> tempUnitNameList = userManagerService.listSubUnitNameWithParent(unitName);
					if(ListTools.isNotEmpty(tempUnitNameList)){
						for(String tempUnit:tempUnitNameList){
							if(!ListTools.contains(unUnitNameList, tempUnit)){
								unUnitNameList.add(tempUnit);
							}
						}
					}
				}
			} 
		}
		return unUnitNameList;
	}
	
	/**
	 * 获取不需要考勤的人员
	 * @return
	 * @throws Exception 
	 */
	protected  List<String> getUnPersonNameList() throws Exception {
		List<String> personNameList = new ArrayList<String>();
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isNotEmpty(employeeName) && !ListTools.contains(personNameList, employeeName)){
					personNameList.add(employeeName);
				}
			}
		}
		return personNameList;
	}
}

package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.ThisApplication;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceStatisticServiceAdv {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceStatisticServiceAdv.class );
//	private AttendanceWorkDayConfigService attendanceWorkDayConfigService = new AttendanceWorkDayConfigService();
	private AttendanceStatisticRequireLogService attendanceStatisticRequireLogService = new AttendanceStatisticRequireLogService();
	private AttendanceStatisticService attendanceStatisticService = new AttendanceStatisticService();
//	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();

	/**
	 * 根据统计年份月份列表来对每个月的数据进行统计
	 */
	public void doStatistic( Boolean debugger ) {
		logger.debug( debugger, ">>>>>>>>>>>系统正准备开始进行数据统计......" );
//		AttendanceStatisticalCycle attendanceStatisticalCycle  = null;
//		List<AttendanceWorkDayConfig> workDayConfigList = null;
		List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
//		Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = null;
		
//		try {//先查询所有的法定节假日和工作日配置列表
//			workDayConfigList = attendanceWorkDayConfigService.getAllWorkDayConfigWithCache(debugger);
//		} catch ( Exception e ) {
//			logger.warn("【统计】系统在查询当月有打卡记录的员工姓名列表时发生异常！" );
//			logger.error(e);
//		}
//
//		try{//查询所有的考勤统计周期信息，并且组织成MAP
//			statisticalCycleMap = attendanceStatisticalCycleService.getAllStatisticalCycleMapWithCache(debugger);
//		}catch(Exception e){
//			logger.warn( "【统计】系统在查询并且组织所有的考勤统计周期信息时发生异常。" );
//			logger.error(e);
//		}
		
		//先处理所有的统计错误
		try {
			logger.debug( debugger, ">>>>>>>>>>>准备处理恢复的统计错误信息, 所有错误统计将会重新计算......" );
			attendanceStatisticRequireLogService.resetStatisticError();
		} catch (Exception e) {
			logger.warn("【统计】系统在重置统计错误信息时发生异常！" );
			logger.error(e);
		}
		
		
		//统计类型:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
		//统计处理状态：WAITING|PROCESSING|COMPLETE|ERROR
		//1  员工每月统计
		//  1.1  先查询需要员工月度统计的统计需求有多少  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>系统准备统计[员工每月统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "PERSON_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询员工月度统计需求列表时发生异常！" );
			logger.error(e);
		}
		
		if( attendanceStatisticRequireLogList == null ){
			attendanceStatisticRequireLogList = new ArrayList<AttendanceStatisticRequireLog>();
		}		
		
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//统计考勤数据，发送到执行队列
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>系统准备统计[员工每月统计]， 员工：" + log.getStatisticKey() + ", 统计月份:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
//				try {
//					attendanceStatisticalCycle = attendanceStatisticalCycleService.getStatisticCycleByEmployee( log, statisticalCycleMap, debugger );
//				} catch (Exception e) {
//					logger.warn("【统计】系统在根据统计需求记录信息查询统计周期信息时发生异常！" );
//					logger.error(e);
//				}
//				if( attendanceStatisticalCycle != null ){
//					try{
//						attendanceStatisticService.statisticEmployeeAttendanceForMonth( log, attendanceStatisticalCycle, workDayConfigList, statisticalCycleMap);
//					}catch(Exception e){
//						logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
//						logger.error(e);
//					}
//				}
			}
		}
		
		
		//2  组织每月统计
		//  2.1 先查询需要组织月度统计的统计需求有多少  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>系统准备统计[组织每月统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "UNIT_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询组织月度统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//统计考勤数据，发送到执行队列
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>系统准备统计[组织每月统计]， 组织：" + log.getStatisticKey() + ", 统计月份:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
//				try{
//					attendanceStatisticService.statisticUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap );
//				}catch(Exception e){
//					logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
//					logger.error(e);
//				}
			}
		}		
		
		//3  顶层组织每月统计
		//  3.1 先查询需要顶层组织月度统计的统计需求有多少  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>系统准备统计[顶层组织每月统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "TOPUNIT_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询顶层组织月度统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//统计考勤数据，发送到执行队列
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>系统准备统计[顶层组织每月统计]， 顶层组织：" + log.getStatisticKey() + ", 统计月份:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
//				try{
//					attendanceStatisticService.statisticTopUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap);
//				}catch(Exception e){
//					logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
//					logger.error(e);
//				}
			}
		}		
		
		//4  组织每日统计
		//  4.1 先查询需要组织每日统计的统计需求有多少  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>系统准备统计[组织每日统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "UNIT_PER_DAY", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询组织每日统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//统计考勤数据，发送到执行队列
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>系统准备统计[组织每月统计]， 组织：" + log.getStatisticKey() + ", 统计日期:" + log.getStatisticDay() );
//				try{
//					attendanceStatisticService.statisticUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap, debugger );
//				}catch(Exception e){
//					logger.warn( "【统计】系统在根据需求进行组织每日打卡记录分析结果统计时发生异常。" );
//					logger.error(e);
//				}
			}
		}
		
		//5  顶层组织每日统计
		//  5.1 先查询需要顶层组织每日统计的统计需求有多少  AttendanceStatisticRequireLog
		logger.debug( debugger, ">>>>>>>>>>>系统准备统计[顶层组织每日统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "TOPUNIT_PER_DAY", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询顶层组织每日统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog log : attendanceStatisticRequireLogList ){
				//统计考勤数据，发送到执行队列
				try {
					ThisApplication.detailStatisticQueue.send( log.getId() );
				} catch ( Exception e1 ) {
					e1.printStackTrace();
				}
//				logger.debug( debugger, ">>>>>>>>>>>系统准备统计[顶层组织每月统计]， 顶层组织：" + log.getStatisticKey() + ", 统计日期:" + log.getStatisticDay() );
//				try{
//					attendanceStatisticService.statisticTopUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap );
//				}catch(Exception e){
//					logger.warn( "【统计】系统在根据需求进行顶层组织每日打卡记录分析结果统计时发生异常。" );
//					logger.error(e);
//				}
			}
		}
		logger.debug( debugger, ">>>>>>>>>>>系统数据统计运行完成." );
	}

	public List<String> listPersonForMonthByUserYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByUserYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticPersonForMonth> listPersonForMonth(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listPersonForMonthByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByUnitYearAndMonth( emc, unitNameList, year, month);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	//排除不需要的组织和人员
	public List<String> listPersonForMonthByUnitYearMonthAndUn(List<String> unitNameList,List<String> unUnitNameList,List<String> personNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByUnitYearMonthAndUn( emc, unitNameList,unUnitNameList,personNameList, year, month);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUnitForMonthByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForMonthByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listUnitForMonthByUnitYearAndMonth( String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForMonthByUnitYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<StatisticUnitForMonth> listUnitForMonth( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员缺勤人次总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_AbsenceDayCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_AbsenceDayCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员请假人次总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumUnitForMonth_OnSelfHolidayCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_OnSelfHolidayCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员迟到次数总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LateCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_LateCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员早退人次总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LeaveEarlyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_LeaveEarlyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员签到人次总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_OnDutyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_OnDutyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员签退人次总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_OffDutyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_OffDutyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员异常打卡次数总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_AbNormalDutyCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_AbNormalDutyCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员工时不足人次总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumUnitForMonth_LackOfTimeCount_ByUnitYearAndMonth(List<String> unitNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_LackOfTimeCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员出勤人天总和
	 * @param unitNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Object sumUnitForMonth_AttendanceDayCount_ByUnitYearAndMonth(List<String> unitNameList,
			String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumUnitForMonth_AttendanceDayCount_ByUnitYearAndMonth( emc, unitNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticTopUnitForMonth_ByTopUnitYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticTopUnitForMonth_ByTopUnitYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticTopUnitForMonth> listTopUnitForMonth( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listTopUnitForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticUnitForDay_ByUnitDayYearAndMonth(List<String> unitNames, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticUnitForDay_ByUnitDayYearAndMonth( emc, unitNames, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticUnitForDay> listUnitForDay(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listUnitForDay( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticTopUnitForDay_ByNameYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticTopUnitForDay_ByNameYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticTopUnitForDay> listTopUnitForDay(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listTopUnitForDay( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

    public void statisticEmployeeAttendanceForMonth(AttendanceStatisticRequireLog log, AttendanceStatisticalCycle attendanceStatisticalCycle, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticEmployeeAttendanceForMonth( log, attendanceStatisticalCycle, workDayConfigList, statisticalCycleMap);
	}

	public void statisticUnitAttendanceForMonth(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap );
	}

	public void statisticTopUnitAttendanceForMonth(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticTopUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap);
	}

	public void statisticUnitAttendanceForDay(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap, boolean b) throws Exception {
		attendanceStatisticService.statisticUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap, false );
	}

	public void statisticTopUnitAttendanceForDay(AttendanceStatisticRequireLog log, List<AttendanceWorkDayConfig> workDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap) throws Exception {
		attendanceStatisticService.statisticTopUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap );
	}
}

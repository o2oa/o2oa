package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

public class AttendanceStatisticServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticServiceAdv.class );
	private AttendanceWorkDayConfigService attendanceWorkDayConfigService = new AttendanceWorkDayConfigService();
	private AttendanceStatisticRequireLogService attendanceStatisticRequireLogService = new AttendanceStatisticRequireLogService();
	private AttendanceStatisticService attendanceStatisticService = new AttendanceStatisticService();
	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();

	/**
	 * 根据统计年份月份列表来对每个月的数据进行统计
	 */
	public void doStatistic() {
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统正准备开始进行数据统计......" );
		AttendanceStatisticalCycle attendanceStatisticalCycle  = null;
		List<AttendanceWorkDayConfig> workDayConfigList = null;
		List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		
		try {//先查询所有的法定节假日和工作日配置列表
			//logger.info( ">>>>>>>>>>>>>>>>>>>>>准备所有的法定节假日和工作日配置列表......" );
			workDayConfigList = attendanceWorkDayConfigService.listAll();
		} catch ( Exception e ) {
			logger.warn("【统计】系统在查询当月有打卡记录的员工姓名列表时发生异常！" );
			logger.error(e);
		}
		
		try{//查询所有的考勤统计周期信息，并且组织成MAP
			//logger.info( ">>>>>>>>>>>>>>>>>>>>>准备所有的考勤统计周期信息......" );
			companyAttendanceStatisticalCycleMap = attendanceStatisticalCycleService.getCycleMapFormAllCycles();
		}catch(Exception e){
			logger.warn( "【统计】系统在查询并且组织所有的考勤统计周期信息时发生异常。" );
			logger.error(e);
		}
		
		//先处理所有的统计错误
		try {
			//logger.info( ">>>>>>>>>>>>>>>>>>>>>准备处理恢复的统计错误信息, 所有错误统计将会重新计算......" );
			attendanceStatisticRequireLogService.resetStatisticError();
		} catch (Exception e) {
			logger.warn("【统计】系统在重置统计错误信息时发生异常！" );
			logger.error(e);
		}
		
		
		//统计类型:PERSON_PER_MONTH|DEPARTMENT_PER_MONTH|COMPANY_PER_MONTH|DEPARTMENT_PER_DAY|COMPANY_PER_DAY
		//统计处理状态：WAITING|PROCESSING|COMPLETE|ERROR
		//1  员工每月统计
		//  1.1  先查询需要员工月度统计的统计需求有多少  AttendanceStatisticRequireLog
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[员工每月统计]......" );
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
			for( AttendanceStatisticRequireLog attendanceStatisticRequireLog : attendanceStatisticRequireLogList ){
				//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[员工每月统计]， 员工：" + attendanceStatisticRequireLog.getStatisticKey() + ", 统计月份:" + attendanceStatisticRequireLog.getStatisticYear() + "-" +attendanceStatisticRequireLog.getStatisticMonth() );
				try {
					attendanceStatisticalCycle = attendanceStatisticalCycleService.getStatisticCycleByEmployee( attendanceStatisticRequireLog, companyAttendanceStatisticalCycleMap );
				} catch (Exception e) {
					logger.warn("【统计】系统在根据统计需求记录信息查询统计周期信息时发生异常！" );
					logger.error(e);
				}
				if( attendanceStatisticalCycle != null ){
					try{
						attendanceStatisticService.statisticEmployeeAttendanceForMonth( attendanceStatisticRequireLog, attendanceStatisticalCycle, workDayConfigList, companyAttendanceStatisticalCycleMap);
					}catch(Exception e){
						logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
						logger.error(e);
					}
				}
			}
		}
		
		
		//2  部门每月统计
		//  2.1 先查询需要部门月度统计的统计需求有多少  AttendanceStatisticRequireLog
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[部门每月统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "DEPARTMENT_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询部门月度统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog attendanceStatisticRequireLog : attendanceStatisticRequireLogList ){
				//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[部门每月统计]， 部门：" + attendanceStatisticRequireLog.getStatisticKey() + ", 统计月份:" + attendanceStatisticRequireLog.getStatisticYear() + "-" +attendanceStatisticRequireLog.getStatisticMonth() );
				try{
					attendanceStatisticService.statisticDepartmentAttendanceForMonth( attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
				}catch(Exception e){
					logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
					logger.error(e);
				}
			}
		}		
		
		//3  公司每月统计
		//  3.1 先查询需要公司月度统计的统计需求有多少  AttendanceStatisticRequireLog
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[公司每月统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "COMPANY_PER_MONTH", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询公司月度统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog attendanceStatisticRequireLog : attendanceStatisticRequireLogList ){
				//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[公司每月统计]， 公司：" + attendanceStatisticRequireLog.getStatisticKey() + ", 统计月份:" + attendanceStatisticRequireLog.getStatisticYear() + "-" +attendanceStatisticRequireLog.getStatisticMonth() );
				try{
					attendanceStatisticService.statisticCompanyAttendanceForMonth( attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap);
				}catch(Exception e){
					logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
					logger.error(e);
				}
			}
		}		
		
		//4  部门每日统计
		//  4.1 先查询需要部门每日统计的统计需求有多少  AttendanceStatisticRequireLog
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[部门每日统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "DEPARTMENT_PER_DAY", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询部门每日统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog attendanceStatisticRequireLog : attendanceStatisticRequireLogList ){
				//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[部门每月统计]， 部门：" + attendanceStatisticRequireLog.getStatisticKey() + ", 统计日期:" + attendanceStatisticRequireLog.getStatisticDay() );
				try{
					attendanceStatisticService.statisticDepartmentAttendanceForDay( attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
				}catch(Exception e){
					logger.warn( "【统计】系统在根据需求进行部门每日打卡记录分析结果统计时发生异常。" );
					logger.error(e);
				}
			}
		}
		
		//5  公司每日统计
		//  5.1 先查询需要公司每日统计的统计需求有多少  AttendanceStatisticRequireLog
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[公司每日统计]......" );
		try {
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogService.listByStatisticTypeAndStatus( "COMPANY_PER_DAY", "WAITING" );
		} catch (Exception e) {
			logger.warn("【统计】系统在查询公司每日统计需求列表时发生异常！" );
			logger.error(e);
		}
		if( attendanceStatisticRequireLogList != null && !attendanceStatisticRequireLogList.isEmpty() ){
			for( AttendanceStatisticRequireLog attendanceStatisticRequireLog : attendanceStatisticRequireLogList ){
				//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统准备统计[公司每月统计]， 公司：" + attendanceStatisticRequireLog.getStatisticKey() + ", 统计日期:" + attendanceStatisticRequireLog.getStatisticDay() );
				try{
					attendanceStatisticService.statisticCompanyAttendanceForDay( attendanceStatisticRequireLog, workDayConfigList, companyAttendanceStatisticalCycleMap );
				}catch(Exception e){
					logger.warn( "【统计】系统在根据需求进行公司每日打卡记录分析结果统计时发生异常。" );
					logger.error(e);
				}
			}
		}
		//logger.info( ">>>>>>>>>>>>>>>>>>>>>系统数据统计运行完成." );
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

	public List<String> listPersonForMonthByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listPersonForMonthByDepartmentYearAndMonth( emc, departmentNameList, year, month);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listDepartmentForMonthByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listDepartmentForMonthByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listDepartmentForMonthByDepartmentYearAndMonth( String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listDepartmentForMonthByDepartmentYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<StatisticDepartmentForMonth> listDepartmentForMonth( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listDepartmentForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员缺勤人次总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumDepartmentForMonth_AbsenceDayCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_AbsenceDayCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员请假人次总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Double sumDepartmentForMonth_OnSelfHolidayCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_OnSelfHolidayCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员迟到次数总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_LateCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_LateCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员早退人次总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_LeaveEarlyCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_LeaveEarlyCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员签到人次总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_OnDutyCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_OnDutyCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员签退人次总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_OffDutyCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_OffDutyCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员异常打卡次数总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_AbNormalDutyCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_AbNormalDutyCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员工时不足人次总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Long sumDepartmentForMonth_LackOfTimeCount_ByDepartmentYearAndMonth(List<String> departmentNameList, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_LackOfTimeCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据部门名称，统计年月，统计公司所有人员出勤人天总和
	 * @param departmentNameList
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Object sumDepartmentForMonth_AttendanceDayCount_ByDepartmentYearAndMonth(List<String> departmentNameList,
			String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.sumDepartmentForMonth_AttendanceDayCount_ByDepartmentYearAndMonth( emc, departmentNameList, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticCompanyForMonth_ByCompanyYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticCompanyForMonth_ByCompanyYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticCompanyForMonth> listCompanyForMonth( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listCompanyForMonth( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticDepartmentForDay_ByDepartmentDayYearAndMonth(List<String> departmentNames, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticDepartmentForDay_ByDepartmentDayYearAndMonth( emc, departmentNames, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticDepartmentForDay> listDepartmentForDay(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listDepartmentForDay( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listStatisticCompanyForDay_ByNameYearAndMonth(String name, String year, String month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listStatisticCompanyForDay_ByNameYearAndMonth( emc, name, year, month );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<StatisticCompanyForDay> listCompanyForDay(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceStatisticService.listCompanyForDay( emc, ids );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}

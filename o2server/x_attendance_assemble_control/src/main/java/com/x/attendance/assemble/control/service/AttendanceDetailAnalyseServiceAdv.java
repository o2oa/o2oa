package com.x.attendance.assemble.control.service;

import com.x.attendance.entity.*;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseServiceAdv {
	
	private AttendanceDetailAnalyseService attendanceDetailAnalyseService = new AttendanceDetailAnalyseService();
	
	public Boolean analyseAttendanceDetail( AttendanceDetail detail, AttendanceScheduleSetting attendanceScheduleSetting, List<AttendanceSelfHoliday> selfHolidays, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailAnalyseService.analyseAttendanceDetail( emc, detail, attendanceScheduleSetting, selfHolidays, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Boolean analyseAttendanceDetail( AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailAnalyseService.analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, debugger );	
		} catch ( Exception e ) {
			throw e;
		}
	}


	public List<String> getAnalyseAttendanceDetailIds( String employeeName, Date startTime, Date endTime ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailAnalyseService.getAnalyseAttendanceDetailIds( emc, employeeName, startTime, endTime );	
		} catch ( Exception e ) {
			throw e;
		}
	}


	public Boolean analyseAttendanceDetails(String employeeName, Date startTime, Date endTime, Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap, Boolean debugger ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailAnalyseService.analyseAttendanceDetails( emc, employeeName, startTime, endTime, topUnitAttendanceStatisticalCycleMap, debugger );	
		} catch ( Exception e ) {
			throw e;
		}
	}

}

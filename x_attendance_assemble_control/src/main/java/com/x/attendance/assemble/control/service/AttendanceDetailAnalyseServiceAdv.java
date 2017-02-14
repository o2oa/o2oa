package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailAnalyseServiceAdv {
	
	private AttendanceDetailAnalyseService attendanceDetailAnalyseService = new AttendanceDetailAnalyseService();
	
	
	public Boolean analyseAttendanceDetail( AttendanceDetail detail, List<AttendanceWorkDayConfig> attendanceWorkDayConfigList, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailAnalyseService.analyseAttendanceDetail( emc, detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap );	
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


	public Boolean analyseAttendanceDetails(String employeeName, Date startTime, Date endTime, Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailAnalyseService.analyseAttendanceDetails( emc, employeeName, startTime, endTime, companyAttendanceStatisticalCycleMap );	
		} catch ( Exception e ) {
			throw e;
		}
	}
}

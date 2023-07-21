package com.x.attendance.assemble.control.processor;

import java.util.List;
import java.util.Map;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;

public class EntitySupplementData extends AbStractDataForOperator {
	
	private Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null; 
	
	private AttendanceEmployeeConfig attendanceEmployeeConfig = null;
	
	private String cycleYear = null;
	
	private String cycleMonth = null;

	public EntitySupplementData( String data_type, String cycleYear, String cycleMonth,
			AttendanceEmployeeConfig attendanceEmployeeConfig, 
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap
			) {
		super();
		this.attendanceEmployeeConfig = attendanceEmployeeConfig;
		this.data_type = data_type;
		this.topUnitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap;
		this.cycleYear = cycleYear;
		this.cycleMonth = cycleMonth;
	}
	
	public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getTopUnitAttendanceStatisticalCycleMap() {
		return topUnitAttendanceStatisticalCycleMap;
	}

	public String getCycleYear() {
		return cycleYear;
	}

	public String getCycleMonth() {
		return cycleMonth;
	}

	public void setTopUnitAttendanceStatisticalCycleMap(
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) {
		this.topUnitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap;
	}

	public void setCycleYear(String cycleYear) {
		this.cycleYear = cycleYear;
	}

	public void setCycleMonth(String cycleMonth) {
		this.cycleMonth = cycleMonth;
	}

	public AttendanceEmployeeConfig getAttendanceEmployeeConfig() {
		return attendanceEmployeeConfig;
	}

	public void setAttendanceEmployeeConfig(AttendanceEmployeeConfig attendanceEmployeeConfig) {
		this.attendanceEmployeeConfig = attendanceEmployeeConfig;
	}
	
}

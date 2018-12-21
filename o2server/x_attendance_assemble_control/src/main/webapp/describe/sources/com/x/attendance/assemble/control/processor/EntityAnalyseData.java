package com.x.attendance.assemble.control.processor;

import java.util.List;
import java.util.Map;

import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;

public class EntityAnalyseData extends AbStractDataForOperator {
	
	private Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null; 
	private List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
	private String personName = null;
	private List<String> detailIds = null;
	
	public EntityAnalyseData(String data_type, String personName, List<String> detailIds,
			List<AttendanceWorkDayConfig> attendanceWorkDayConfigList,
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap ) {
		super();
		this.data_type = data_type;
		this.attendanceWorkDayConfigList = attendanceWorkDayConfigList;
		this.topUnitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap;
		this.personName = personName;
		this.detailIds = detailIds;
	}

	public Map<String, Map<String, List<AttendanceStatisticalCycle>>> getTopUnitAttendanceStatisticalCycleMap() {
		return topUnitAttendanceStatisticalCycleMap;
	}

	public void setTopUnitAttendanceStatisticalCycleMap(
			Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap) {
		this.topUnitAttendanceStatisticalCycleMap = topUnitAttendanceStatisticalCycleMap;
	}

	public List<AttendanceWorkDayConfig> getAttendanceWorkDayConfigList() {
		return attendanceWorkDayConfigList;
	}

	public String getPersonName() {
		return personName;
	}

	public List<String> getDetailIds() {
		return detailIds;
	}

	public void setAttendanceWorkDayConfigList(List<AttendanceWorkDayConfig> attendanceWorkDayConfigList) {
		this.attendanceWorkDayConfigList = attendanceWorkDayConfigList;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public void setDetailIds(List<String> detailIds) {
		this.detailIds = detailIds;
	}
}

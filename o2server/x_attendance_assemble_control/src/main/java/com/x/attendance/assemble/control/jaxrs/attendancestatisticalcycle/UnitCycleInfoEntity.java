package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import java.util.List;

import com.x.attendance.entity.AttendanceStatisticalCycle;

/**
 * 存储组织信息的对象
 * @author O2LEE
 *
 */
public class UnitCycleInfoEntity {
	
	private String unitName;
	
	private String topUnitName;
	
	private List<AttendanceStatisticalCycle> unitCycles = null;
	
	public UnitCycleInfoEntity() {}
	
	public UnitCycleInfoEntity(String unitName, String topUnitName, List<AttendanceStatisticalCycle> unitCycles) {
		super();
		this.unitName = unitName;
		this.topUnitName = topUnitName;
		this.unitCycles = unitCycles;
	}

	public String getUnitName() {
		return unitName;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public List<AttendanceStatisticalCycle> getUnitCycles() {
		return unitCycles;
	}

	public void setUnitCycles(List<AttendanceStatisticalCycle> unitCycles) {
		this.unitCycles = unitCycles;
	}	
}


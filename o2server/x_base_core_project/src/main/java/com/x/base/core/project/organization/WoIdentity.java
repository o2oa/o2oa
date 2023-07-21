package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

public class WoIdentity extends Identity {

	@FieldDescribe("组织对象")
	private WoUnit woUnit;

	@FieldDescribe("组织职务对象")
	private List<WoUnitDuty> woUnitDutyList;

	public WoUnit getWoUnit() {
		return woUnit;
	}

	public void setWoUnit(WoUnit woUnit) {
		this.woUnit = woUnit;
	}

	public List<WoUnitDuty> getWoUnitDutyList() {
		return woUnitDutyList;
	}

	public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
		this.woUnitDutyList = woUnitDutyList;
	}
}

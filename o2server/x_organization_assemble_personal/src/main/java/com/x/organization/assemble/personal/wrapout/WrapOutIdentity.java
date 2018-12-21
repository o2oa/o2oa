package com.x.organization.assemble.personal.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.organization.core.entity.Identity;

public class WrapOutIdentity extends Identity {

	private String unitName;
	private String topUnit;
	private String topUnitName;
	private String personName;
	private String onlineStatus;
	private List<WrapOutUnitDuty> unitDutyList;

	private static final long serialVersionUID = -7448825297703157229L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getTopUnit() {
		return topUnit;
	}

	public void setTopUnit(String topUnit) {
		this.topUnit = topUnit;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public List<WrapOutUnitDuty> getUnitDutyList() {
		return unitDutyList;
	}

	public void setUnitDutyList(List<WrapOutUnitDuty> unitDutyList) {
		this.unitDutyList = unitDutyList;
	}

}

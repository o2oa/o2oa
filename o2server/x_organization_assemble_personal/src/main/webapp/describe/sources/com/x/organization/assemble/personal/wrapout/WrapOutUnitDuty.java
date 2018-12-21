package com.x.organization.assemble.personal.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.organization.core.entity.UnitDuty;

public class WrapOutUnitDuty extends UnitDuty {

	private static final long serialVersionUID = 2411311797461626124L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
	private Long rank;

	private String departmentName;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

}

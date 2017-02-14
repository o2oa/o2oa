package com.x.organization.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Department;

@Wrap(Department.class)
public class WrapOutDepartment extends Department {

	private static final long serialVersionUID = -7221107386984777695L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private List<WrapOutDepartment> departmentList;
	private List<WrapOutIdentity> identityList;

	private Long departmentSubDirectCount;
	private Long identitySubDirectCount;

	private Long rank;

	public Long getDepartmentSubDirectCount() {
		return departmentSubDirectCount;
	}

	public void setDepartmentSubDirectCount(Long departmentSubDirectCount) {
		this.departmentSubDirectCount = departmentSubDirectCount;
	}

	public Long getIdentitySubDirectCount() {
		return identitySubDirectCount;
	}

	public void setIdentitySubDirectCount(Long identitySubDirectCount) {
		this.identitySubDirectCount = identitySubDirectCount;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WrapOutDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<WrapOutDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<WrapOutIdentity> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<WrapOutIdentity> identityList) {
		this.identityList = identityList;
	}

}

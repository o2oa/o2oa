package com.x.organization.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Person;

@Wrap(Person.class)
public class WrapOutPerson extends Person {

	private static final long serialVersionUID = -8456354949288335211L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private List<WrapOutIdentity> identityList;

	private List<WrapOutCompanyDuty> companyDutyList;

	private List<WrapOutDepartmentDuty> departmentDutyList;

	private String onlineStatus;

	static {
		Excludes.add("password");
	}

	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WrapOutIdentity> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<WrapOutIdentity> identityList) {
		this.identityList = identityList;
	}

	public List<WrapOutCompanyDuty> getCompanyDutyList() {
		return companyDutyList;
	}

	public void setCompanyDutyList(List<WrapOutCompanyDuty> companyDutyList) {
		this.companyDutyList = companyDutyList;
	}

	public List<WrapOutDepartmentDuty> getDepartmentDutyList() {
		return departmentDutyList;
	}

	public void setDepartmentDutyList(List<WrapOutDepartmentDuty> departmentDutyList) {
		this.departmentDutyList = departmentDutyList;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

}

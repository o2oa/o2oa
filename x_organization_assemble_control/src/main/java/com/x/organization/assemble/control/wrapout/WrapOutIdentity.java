package com.x.organization.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Identity;

@Wrap(Identity.class)
public class WrapOutIdentity extends Identity {

	private String departmentName;
	private String company;
	private String companyName;
	private String personName;
	private String onlineStatus;
	private List<WrapOutCompanyDuty> companyDutyList;
	private List<WrapOutDepartmentDuty> departmentDutyList;

	private static final long serialVersionUID = -7448825297703157229L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
	private Long rank;

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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

}

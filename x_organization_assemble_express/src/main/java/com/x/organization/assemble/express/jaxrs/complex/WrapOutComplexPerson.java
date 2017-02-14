package com.x.organization.assemble.express.jaxrs.complex;

import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutIdentity;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;

@Wrap(Person.class)
public class WrapOutComplexPerson extends WrapOutPerson {

	private static final long serialVersionUID = 9214654057449831029L;

	public WrapOutComplexPerson() throws Exception {
		super();
	}

	private List<WrapOutIdentity> identityList;

	private List<WrapOutCompanyDuty> companyDutyList;

	private List<WrapOutDepartmentDuty> departmentDutyList;

	private String onlineStatus;

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
package com.x.processplatform.assemble.bam;

import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.CompanyStubs;
import com.x.processplatform.assemble.bam.stub.DepartmentStubs;
import com.x.processplatform.assemble.bam.stub.PersonStubs;

public class State {

	private ApplicationStubs applicationStubs = new ApplicationStubs();

	private CompanyStubs companyStubs = new CompanyStubs();

	private DepartmentStubs departmentStubs = new DepartmentStubs();

	private PersonStubs personStubs = new PersonStubs();

	private WrapOutMap summary = new WrapOutMap();

	private WrapOutMap running = new WrapOutMap();

	private WrapOutMap organization = new WrapOutMap();

	private WrapOutMap category = new WrapOutMap();

	public WrapOutMap getSummary() {
		return summary;
	}

	public void setSummary(WrapOutMap summary) {
		this.summary = summary;
	}

	public ApplicationStubs getApplicationStubs() {
		return applicationStubs;
	}

	public void setApplicationStubs(ApplicationStubs applicationStubs) {
		this.applicationStubs = applicationStubs;
	}

	public CompanyStubs getCompanyStubs() {
		return companyStubs;
	}

	public void setCompanyStubs(CompanyStubs companyStubs) {
		this.companyStubs = companyStubs;
	}

	public DepartmentStubs getDepartmentStubs() {
		return departmentStubs;
	}

	public void setDepartmentStubs(DepartmentStubs departmentStubs) {
		this.departmentStubs = departmentStubs;
	}

	public PersonStubs getPersonStubs() {
		return personStubs;
	}

	public void setPersonStubs(PersonStubs personStubs) {
		this.personStubs = personStubs;
	}

	public WrapOutMap getOrganization() {
		return organization;
	}

	public void setOrganization(WrapOutMap organization) {
		this.organization = organization;
	}

	public WrapOutMap getRunning() {
		return running;
	}

	public void setRunning(WrapOutMap running) {
		this.running = running;
	}

	public WrapOutMap getCategory() {
		return category;
	}

	public void setCategory(WrapOutMap category) {
		this.category = category;
	}

}

package com.x.processplatform.assemble.bam;

import com.x.processplatform.assemble.bam.stub.ApplicationStubs;
import com.x.processplatform.assemble.bam.stub.CompanyStubs;

public class Period {

	/* 在 period startTask中选择需要的ApplicationStub和CompanyStub */
	private ApplicationStubs startTaskApplicationStubs = new ApplicationStubs();
	private CompanyStubs startTaskCompanyStubs = new CompanyStubs();
	/* 在 period completedTask中选择需要的ApplicationStub和CompanyStub */
	private ApplicationStubs completedTaskApplicationStubs = new ApplicationStubs();
	private CompanyStubs completedTaskCompanyStubs = new CompanyStubs();
	/* 在 period expiredTask中选择需要的ApplicationStub和CompanyStub */
	private ApplicationStubs expiredTaskApplicationStubs = new ApplicationStubs();
	private CompanyStubs expiredTaskCompanyStubs = new CompanyStubs();
	/* 在 period startWork中选择需要的ApplicationStub和CompanyStub */
	private ApplicationStubs startWorkApplicationStubs = new ApplicationStubs();
	private CompanyStubs startWorkCompanyStubs = new CompanyStubs();
	/* 在 period completedWork中选择需要的ApplicationStub和CompanyStub */
	private ApplicationStubs completedWorkApplicationStubs = new ApplicationStubs();
	private CompanyStubs completedWorkCompanyStubs = new CompanyStubs();
	/* 在 period expiredWork中选择需要的ApplicationStub和CompanyStub */
	private ApplicationStubs expiredWorkApplicationStubs = new ApplicationStubs();
	private CompanyStubs expiredWorkCompanyStubs = new CompanyStubs();
	public ApplicationStubs getStartTaskApplicationStubs() {
		return startTaskApplicationStubs;
	}
	public void setStartTaskApplicationStubs(ApplicationStubs startTaskApplicationStubs) {
		this.startTaskApplicationStubs = startTaskApplicationStubs;
	}
	public CompanyStubs getStartTaskCompanyStubs() {
		return startTaskCompanyStubs;
	}
	public void setStartTaskCompanyStubs(CompanyStubs startTaskCompanyStubs) {
		this.startTaskCompanyStubs = startTaskCompanyStubs;
	}
	public ApplicationStubs getCompletedTaskApplicationStubs() {
		return completedTaskApplicationStubs;
	}
	public void setCompletedTaskApplicationStubs(ApplicationStubs completedTaskApplicationStubs) {
		this.completedTaskApplicationStubs = completedTaskApplicationStubs;
	}
	public CompanyStubs getCompletedTaskCompanyStubs() {
		return completedTaskCompanyStubs;
	}
	public void setCompletedTaskCompanyStubs(CompanyStubs completedTaskCompanyStubs) {
		this.completedTaskCompanyStubs = completedTaskCompanyStubs;
	}
	public ApplicationStubs getExpiredTaskApplicationStubs() {
		return expiredTaskApplicationStubs;
	}
	public void setExpiredTaskApplicationStubs(ApplicationStubs expiredTaskApplicationStubs) {
		this.expiredTaskApplicationStubs = expiredTaskApplicationStubs;
	}
	public CompanyStubs getExpiredTaskCompanyStubs() {
		return expiredTaskCompanyStubs;
	}
	public void setExpiredTaskCompanyStubs(CompanyStubs expiredTaskCompanyStubs) {
		this.expiredTaskCompanyStubs = expiredTaskCompanyStubs;
	}
	public ApplicationStubs getStartWorkApplicationStubs() {
		return startWorkApplicationStubs;
	}
	public void setStartWorkApplicationStubs(ApplicationStubs startWorkApplicationStubs) {
		this.startWorkApplicationStubs = startWorkApplicationStubs;
	}
	public CompanyStubs getStartWorkCompanyStubs() {
		return startWorkCompanyStubs;
	}
	public void setStartWorkCompanyStubs(CompanyStubs startWorkCompanyStubs) {
		this.startWorkCompanyStubs = startWorkCompanyStubs;
	}
	public ApplicationStubs getCompletedWorkApplicationStubs() {
		return completedWorkApplicationStubs;
	}
	public void setCompletedWorkApplicationStubs(ApplicationStubs completedWorkApplicationStubs) {
		this.completedWorkApplicationStubs = completedWorkApplicationStubs;
	}
	public CompanyStubs getCompletedWorkCompanyStubs() {
		return completedWorkCompanyStubs;
	}
	public void setCompletedWorkCompanyStubs(CompanyStubs completedWorkCompanyStubs) {
		this.completedWorkCompanyStubs = completedWorkCompanyStubs;
	}
	public ApplicationStubs getExpiredWorkApplicationStubs() {
		return expiredWorkApplicationStubs;
	}
	public void setExpiredWorkApplicationStubs(ApplicationStubs expiredWorkApplicationStubs) {
		this.expiredWorkApplicationStubs = expiredWorkApplicationStubs;
	}
	public CompanyStubs getExpiredWorkCompanyStubs() {
		return expiredWorkCompanyStubs;
	}
	public void setExpiredWorkCompanyStubs(CompanyStubs expiredWorkCompanyStubs) {
		this.expiredWorkCompanyStubs = expiredWorkCompanyStubs;
	}

}

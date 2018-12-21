package com.x.strategydeploy.assemble.control.strategy;

public class UserNaviPower {
	boolean isCompanyLeader; //是否是公司管理层1
	boolean isCompanyStrategyManager;//是否是公司战略管理员2
	boolean isCompanyBusinessManager; //是否是公司事务管理员3
	boolean isDeptLeader; //是否是战略负责人(部主管)4
	boolean isDeptStrategyManager;//是否是各部月度汇报员(部门战略管理员)5
	boolean isDeptReporter;//是否是汇报人6
	boolean isCommonEmployee; //是否是普通员工7

	public boolean isCompanyLeader() {
		return isCompanyLeader;
	}

	public void setCompanyLeader(boolean isCompanyLeader) {
		this.isCompanyLeader = isCompanyLeader;
	}

	public boolean isCompanyStrategyManager() {
		return isCompanyStrategyManager;
	}

	public void setCompanyStrategyManager(boolean isCompanyStrategyManager) {
		this.isCompanyStrategyManager = isCompanyStrategyManager;
	}

	public boolean isCompanyBusinessManager() {
		return isCompanyBusinessManager;
	}

	public void setCompanyBusinessManager(boolean isCompanyBusinessManager) {
		this.isCompanyBusinessManager = isCompanyBusinessManager;
	}

	public boolean isDeptLeader() {
		return isDeptLeader;
	}

	public void setDeptLeader(boolean isDeptLeader) {
		this.isDeptLeader = isDeptLeader;
	}

	public boolean isDeptStrategyManager() {
		return isDeptStrategyManager;
	}

	public void setDeptStrategyManager(boolean isDeptStrategyManager) {
		this.isDeptStrategyManager = isDeptStrategyManager;
	}

	public boolean isDeptReporter() {
		return isDeptReporter;
	}

	public void setDeptReporter(boolean isDeptReporter) {
		this.isDeptReporter = isDeptReporter;
	}

	public boolean isCommonEmployee() {
		return isCommonEmployee;
	}

	public void setCommonEmployee(boolean isCommonEmployee) {
		this.isCommonEmployee = isCommonEmployee;
	}

}
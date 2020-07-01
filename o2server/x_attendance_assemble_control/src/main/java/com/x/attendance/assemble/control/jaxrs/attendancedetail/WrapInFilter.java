package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInFilter extends GsonPropertyObject {

	@FieldDescribe("参与查询的员工标识，人员DistinguishedName")
	private String q_empName;

	private List<String> topUnitNames;

	@FieldDescribe("参与查询的顶层组织（公司）标识，组织DistinguishedName")
	private String q_topUnitName;
	
	private List<String> unitNames;

	@FieldDescribe("参与查询的组织（部门）标识，组织DistinguishedName")
	private String q_unitName;

	@FieldDescribe("查询数据的年份, yyyy")
	private String q_year;

	@FieldDescribe("查询数据的月份, mm")
	private String q_month;

	@FieldDescribe("查询数据的统计周期年份，一般与数据日期的年份一致")
	private String cycleYear;

	@FieldDescribe("查询数据的统计周期月份，一般与数据日期的月份一致")
	private String cycleMonth;

	@FieldDescribe("查询数据的具体日期，yyyy-mm-dd")
	private String q_date;

	private int recordStatus = 999;
	
	private Boolean isAbsent = null;
	
	private Boolean isLate = null;
	
	private Boolean isLeaveEarlier = null;
	
	private Boolean isLackOfTime = null;
	
	private String order = "DESC";

	private String key;

	public String getQ_empName() {
		return q_empName;
	}

	public String getCycleYear() {
		return cycleYear;
	}

	public void setCycleYear(String cycleYear) {
		this.cycleYear = cycleYear;
	}

	public String getCycleMonth() {
		return cycleMonth;
	}

	public void setCycleMonth(String cycleMonth) {
		this.cycleMonth = cycleMonth;
	}

	public void setQ_empName(String q_empName) {
		this.q_empName = q_empName;
	}

	public String getQ_year() {
		return q_year;
	}

	public void setQ_year(String q_year) {
		this.q_year = q_year;
	}

	public String getQ_month() {
		return q_month;
	}

	public void setQ_month(String q_month) {
		this.q_month = q_month;
	}

	public List<String> getTopUnitNames() {
		return topUnitNames;
	}

	public void setTopUnitNames(List<String> topUnitNames) {
		this.topUnitNames = topUnitNames;
	}

	public List<String> getUnitNames() {
		return unitNames;
	}

	public void setUnitNames(List<String> unitNames) {
		this.unitNames = unitNames;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getQ_date() {
		return q_date;
	}

	public void setQ_date(String q_date) {
		this.q_date = q_date;
	}
	
	public Boolean getIsAbsent() {
		return isAbsent;
	}

	public void setIsAbsent(Boolean isAbsent) {
		this.isAbsent = isAbsent;
	}

	public Boolean getIsLate() {
		return isLate;
	}

	public void setIsLate(Boolean isLate) {
		this.isLate = isLate;
	}

	public Boolean getIsLeaveEarlier() {
		return isLeaveEarlier;
	}

	public void setIsLeaveEarlier(Boolean isLeaveEarlier) {
		this.isLeaveEarlier = isLeaveEarlier;
	}

	public Boolean getIsLackOfTime() {
		return isLackOfTime;
	}

	public void setIsLackOfTime(Boolean isLackOfTime) {
		this.isLackOfTime = isLackOfTime;
	}

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getQ_topUnitName() {
		return q_topUnitName;
	}

	public void setQ_topUnitName(String q_topUnitName) {
		this.q_topUnitName = q_topUnitName;
	}

	public String getQ_unitName() {
		return q_unitName;
	}

	public void setQ_unitName(String q_unitName) {
		this.q_unitName = q_unitName;
	}
	
}

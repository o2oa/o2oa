package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceDetail.class)
public class WrapInFilter extends GsonPropertyObject {

	private String q_empName;
	
	private List<String> companyNames;
	
	private String q_companyName;
	
	private List<String> departmentNames;
	
	private String q_departmentName;

	private String q_year;
	
	private String q_month;
	
	private String cycleYear;
	
	private String cycleMonth;
	
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

	public List<String> getCompanyNames() {
		return companyNames;
	}

	public void setCompanyNames(List<String> companyNames) {
		this.companyNames = companyNames;
	}

	public List<String> getDepartmentNames() {
		return departmentNames;
	}

	public void setDepartmentNames(List<String> departmentNames) {
		this.departmentNames = departmentNames;
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

	public String getQ_companyName() {
		return q_companyName;
	}

	public void setQ_companyName(String q_companyName) {
		this.q_companyName = q_companyName;
	}

	public String getQ_departmentName() {
		return q_departmentName;
	}

	public void setQ_departmentName(String q_departmentName) {
		this.q_departmentName = q_departmentName;
	}
	
}

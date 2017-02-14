package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;

import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceSelfHoliday.class)
public class WrapInFilter extends GsonPropertyObject {

	private String q_empName;
	
	private List<String> companyNames;
	
	private String q_companyName;
	
	private List<String> departmentNames;
	
	private String q_departmentName;
	
	private String order = "DESC";

	private String key;	

	public String getQ_empName() {
		return q_empName;
	}

	public void setQ_empName(String q_empName) {
		this.q_empName = q_empName;
	}

	public List<String> getCompanyNames() {
		return companyNames;
	}

	public void setCompanyNames(List<String> companyNames) {
		this.companyNames = companyNames;
	}

	public String getQ_companyName() {
		return q_companyName;
	}

	public void setQ_companyName(String q_companyName) {
		this.q_companyName = q_companyName;
	}

	public List<String> getDepartmentNames() {
		return departmentNames;
	}

	public void setDepartmentNames(List<String> departmentNames) {
		this.departmentNames = departmentNames;
	}

	public String getQ_departmentName() {
		return q_departmentName;
	}

	public void setQ_departmentName(String q_departmentName) {
		this.q_departmentName = q_departmentName;
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

}

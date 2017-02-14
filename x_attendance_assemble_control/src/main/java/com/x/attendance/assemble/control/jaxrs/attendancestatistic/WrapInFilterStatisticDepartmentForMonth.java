package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap(StatisticPersonForMonth.class)
public class WrapInFilterStatisticDepartmentForMonth extends GsonPropertyObject {

	private List<String> companyName;
	private List<String> employeeName;
	private List<String> organizationName;
	private String statisticMonth;
	private String statisticYear;
	private String order = "DESC";
	private String key;

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

	
	public List<String> getCompanyName() {
		return companyName;
	}

	public void setCompanyName(List<String> companyName) {
		this.companyName = companyName;
	}

	public List<String> getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(List<String> employeeName) {
		this.employeeName = employeeName;
	}

	public List<String> getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(List<String> organizationName) {
		this.organizationName = organizationName;
	}

	public String getStatisticMonth() {
		return statisticMonth;
	}

	public void setStatisticMonth(String statisticMonth) {
		this.statisticMonth = statisticMonth;
	}

	public String getStatisticYear() {
		return statisticYear;
	}

	public void setStatisticYear(String statisticYear) {
		this.statisticYear = statisticYear;
	}
	
	
}

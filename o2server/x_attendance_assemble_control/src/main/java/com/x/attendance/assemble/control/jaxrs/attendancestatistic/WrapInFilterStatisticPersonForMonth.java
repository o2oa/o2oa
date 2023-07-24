package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInFilterStatisticPersonForMonth extends GsonPropertyObject {

	private List<String> topUnitName;
	private List<String> employeeName;
	private List<String> unitName;
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

	public List<String> getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(List<String> employeeName) {
		this.employeeName = employeeName;
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

	public List<String> getTopUnitName() {
		return topUnitName;
	}

	public List<String> getUnitName() {
		return unitName;
	}

	public void setTopUnitName(List<String> topUnitName) {
		this.topUnitName = topUnitName;
	}

	public void setUnitName(List<String> unitName) {
		this.unitName = unitName;
	}
	
	
}

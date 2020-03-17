package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInFilter extends GsonPropertyObject {

	private String q_empName;
	
	private List<String> topUnitNames;
	
	private String q_topUnitName;
	
	private List<String> unitNames;
	
	private String q_unitName;
	
	private String order = "DESC";

	private String key;	

	public String getQ_empName() {
		return q_empName;
	}

	public void setQ_empName(String q_empName) {
		this.q_empName = q_empName;
	}

	public List<String> getTopUnitNames() {
		return topUnitNames;
	}

	public void setTopUnitNames(List<String> topUnitNames) {
		this.topUnitNames = topUnitNames;
	}

	public String getQ_topUnitName() {
		return q_topUnitName;
	}

	public void setQ_topUnitName(String q_topUnitName) {
		this.q_topUnitName = q_topUnitName;
	}

	public List<String> getUnitNames() {
		return unitNames;
	}

	public void setUnitNames(List<String> unitNames) {
		this.unitNames = unitNames;
	}

	public String getQ_unitName() {
		return q_unitName;
	}

	public void setQ_unitName(String q_unitName) {
		this.q_unitName = q_unitName;
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

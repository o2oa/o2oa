package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.List;

import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceAppealInfo.class)
public class WrapInFilterAppeal extends GsonPropertyObject {

	private String detailId;
	private String empName;	
	private String companyName;	
	private String departmentName;
	private String yearString;
	private String monthString;
	private String appealDateString;
	private String recordDateString;
	//审批状态:0-待处理，1-审批通过，-1-审批不能过，2-需要下一次审批
	private int status = 999;
	private String appealReason;
	private String processPerson1;
	private String processPerson2;	
	private String order = "DESC";
	private String key;
	
	private List<NameValueCountPair> orAtrribute;
	
	public String getDetailId() {
		return detailId;
	}
	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getYearString() {
		return yearString;
	}
	public void setYearString(String yearString) {
		this.yearString = yearString;
	}
	public String getMonthString() {
		return monthString;
	}
	public void setMonthString(String monthString) {
		this.monthString = monthString;
	}
	public String getAppealDateString() {
		return appealDateString;
	}
	public void setAppealDateString(String appealDateString) {
		this.appealDateString = appealDateString;
	}
	public String getRecordDateString() {
		return recordDateString;
	}
	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAppealReason() {
		return appealReason;
	}
	public void setAppealReason(String appealReason) {
		this.appealReason = appealReason;
	}
	public String getProcessPerson1() {
		return processPerson1;
	}
	public void setProcessPerson1(String processPerson1) {
		this.processPerson1 = processPerson1;
	}
	public String getProcessPerson2() {
		return processPerson2;
	}
	public void setProcessPerson2(String processPerson2) {
		this.processPerson2 = processPerson2;
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
	public List<NameValueCountPair> getOrAtrribute() {
		return orAtrribute;
	}
	public void setOrAtrribute(List<NameValueCountPair> orAtrribute) {
		this.orAtrribute = orAtrribute;
	}

	
}

package com.x.program.center.welink;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Department extends GsonPropertyObject {

	//{
	//        "deptCode": "1",
	//        "deptNameCn": "产品销售部",
	//        "deptNameEn": "Sales Dept",
	//        "fatherCode": "0",
	//        "deptLevel": "2",
	//        "orderNo": 1,
	//        "hasChildDept": 1,
	//        "corpDeptCode": ""
	//      }

	private String deptCode;
	private String deptNameCn;
	private String deptNameEn;
	private String fatherCode;
	private String deptLevel;
	private String corpDeptCode;
	private Long orderNo;
	private Long hasChildDept;

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getDeptNameCn() {
		return deptNameCn;
	}

	public void setDeptNameCn(String deptNameCn) {
		this.deptNameCn = deptNameCn;
	}

	public String getDeptNameEn() {
		return deptNameEn;
	}

	public void setDeptNameEn(String deptNameEn) {
		this.deptNameEn = deptNameEn;
	}

	public String getFatherCode() {
		return fatherCode;
	}

	public void setFatherCode(String fatherCode) {
		this.fatherCode = fatherCode;
	}

	public String getDeptLevel() {
		return deptLevel;
	}

	public void setDeptLevel(String deptLevel) {
		this.deptLevel = deptLevel;
	}

	public String getCorpDeptCode() {
		return corpDeptCode;
	}

	public void setCorpDeptCode(String corpDeptCode) {
		this.corpDeptCode = corpDeptCode;
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}

	public Long getHasChildDept() {
		return hasChildDept;
	}

	public void setHasChildDept(Long hasChildDept) {
		this.hasChildDept = hasChildDept;
	}
}

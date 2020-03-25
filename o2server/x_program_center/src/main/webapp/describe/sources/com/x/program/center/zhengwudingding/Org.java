package com.x.program.center.zhengwudingding;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Org extends GsonPropertyObject {

	// "retData": {
	// "orgNumber": 100016000,
	// "name": "互联网+事业部",
	// "type": 1,
	// "parentId": 1,
	// "fullName": "",
	// "orgCode": "",
	// "postCode": "",
	// "deptHiding": false,
	// "deptPerimits": [],
	// "outerDept": false,
	// "outerPermitDepts": [],
	// "createDeptGroup": false,
	// "orgDeptGroupOwner": null,
	// "deptManagerUseridList": [],
	// "order": 0
	// }
	private Long orgNumber;
	private String name;
	private Integer type;
	private String parentId;
	private String fullName;
	private String orgCode;
	private String postCode;
	private Boolean deptHiding = false;
	private List<String> deptPerimits = new ArrayList<>();
	private Boolean outerDept = false;
	private List<String> outerPermitDepts = new ArrayList<>();
	private Boolean createDeptGroup = false;
	private List<String> deptManagerUseridList = new ArrayList<>();
	private Long order;

	// private String orgDeptGroupOwner = null;
	public Long getOrgNumber() {
		return orgNumber;
	}

	public void setOrgNumber(Long orgNumber) {
		this.orgNumber = orgNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

 

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Boolean getDeptHiding() {
		return deptHiding;
	}

	public void setDeptHiding(Boolean deptHiding) {
		this.deptHiding = deptHiding;
	}

	public List<String> getDeptPerimits() {
		return deptPerimits;
	}

	public void setDeptPerimits(List<String> deptPerimits) {
		this.deptPerimits = deptPerimits;
	}

	public Boolean getOuterDept() {
		return outerDept;
	}

	public void setOuterDept(Boolean outerDept) {
		this.outerDept = outerDept;
	}

	public List<String> getOuterPermitDepts() {
		return outerPermitDepts;
	}

	public void setOuterPermitDepts(List<String> outerPermitDepts) {
		this.outerPermitDepts = outerPermitDepts;
	}

	public Boolean getCreateDeptGroup() {
		return createDeptGroup;
	}

	public void setCreateDeptGroup(Boolean createDeptGroup) {
		this.createDeptGroup = createDeptGroup;
	}

	public List<String> getDeptManagerUseridList() {
		return deptManagerUseridList;
	}

	public void setDeptManagerUseridList(List<String> deptManagerUseridList) {
		this.deptManagerUseridList = deptManagerUseridList;
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orgNumber == null) ? 0 : orgNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Org other = (Org) obj;
		if (orgNumber == null) {
			if (other.orgNumber != null)
				return false;
		} else if (!orgNumber.equals(other.orgNumber))
			return false;
		return true;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}

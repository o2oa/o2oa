package com.x.organization.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Company;

@Wrap(Company.class)
public class WrapOutCompany extends Company {

	private static final long serialVersionUID = -289643105300498768L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long companySubDirectCount;
	private Long departmentSubDirectCount;
	private List<WrapOutCompany> companyList = new ArrayList<>();
	private List<WrapOutDepartment> departmentList = new ArrayList<>();
	private Long rank;

	public Long getCompanySubDirectCount() {
		return companySubDirectCount;
	}

	public void setCompanySubDirectCount(Long companySubDirectCount) {
		this.companySubDirectCount = companySubDirectCount;
	}

	public Long getDepartmentSubDirectCount() {
		return departmentSubDirectCount;
	}

	public void setDepartmentSubDirectCount(Long departmentSubDirectCount) {
		this.departmentSubDirectCount = departmentSubDirectCount;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WrapOutCompany> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<WrapOutCompany> companyList) {
		this.companyList = companyList;
	}

	public List<WrapOutDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<WrapOutDepartment> departmentList) {
		this.departmentList = departmentList;
	}

}

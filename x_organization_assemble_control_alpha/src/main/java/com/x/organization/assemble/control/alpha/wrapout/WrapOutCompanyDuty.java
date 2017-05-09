package com.x.organization.assemble.control.alpha.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.CompanyDuty;

@Wrap(CompanyDuty.class)
public class WrapOutCompanyDuty extends CompanyDuty {

	private static final long serialVersionUID = -7970419642744455166L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private String companyName;

	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}

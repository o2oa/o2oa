package com.x.crm.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.crm.assemble.control.jaxrs.crmbaseconfig.CustomerBaseConfig;

public class WrapOutCustomerBaseConfig extends CustomerBaseConfig {

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private String rootConfigType;

	public String getRootConfigType() {
		return rootConfigType;
	}

	public void setRootConfigType(String rootConfigType) {
		this.rootConfigType = rootConfigType;
	}

	//	private Long rank;
	//
	//	public Long getRank() {
	//		return rank;
	//	}
	//
	//	public void setRank(Long rank) {
	//		this.rank = rank;
	//	}

}

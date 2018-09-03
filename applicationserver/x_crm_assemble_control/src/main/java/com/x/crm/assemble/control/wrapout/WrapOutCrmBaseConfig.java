package com.x.crm.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.crm.core.entity.CrmBaseConfig;

public class WrapOutCrmBaseConfig extends CrmBaseConfig {
	private static final long serialVersionUID = -969148596991975992L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private List<WrapOutCrmBaseConfig> childNodes = null;

	public List<WrapOutCrmBaseConfig> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<WrapOutCrmBaseConfig> childNodes) {
		this.childNodes = childNodes;
	}

	public boolean cFlag = true;

	public boolean iscFlag() {
		return cFlag;
	}

	public void setcFlag(boolean cFlag) {
		this.cFlag = cFlag;
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

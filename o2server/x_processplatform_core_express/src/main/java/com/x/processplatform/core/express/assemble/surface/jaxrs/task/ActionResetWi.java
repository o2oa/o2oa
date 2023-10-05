package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6682653389239676933L;

	private List<String> distinguishedNameList;

	private Boolean keep;

	private String opinion;

	private String routeName;

	public List<String> getDistinguishedNameList() {
		return null == this.distinguishedNameList ? new ArrayList<>() : this.distinguishedNameList;
	}

	public Boolean getKeep() {
		return BooleanUtils.isTrue(this.keep);
	}
	
	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

	public void setKeep(Boolean keep) {
		this.keep = keep;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

}

package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2ResetWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6682653389239676933L;

	@FieldDescribe("重置专有组织标识.")
	private List<String> distinguishedNameList;

	@FieldDescribe("重置身份.")
	private List<String> identityList;

	@Deprecated
	@FieldDescribe("保留待办.")
	private Boolean keep;

	@FieldDescribe("意见.")
	private String opinion;

	@FieldDescribe("路由名称.")
	private String routeName;

	public List<String> getIdentityList() {
		return null == this.identityList ? new ArrayList<>() : this.identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

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

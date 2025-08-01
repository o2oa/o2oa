package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.List;

public class ActionUpdateWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5876504008709428039L;

	@FieldDescribe("用户.")
	private String person;

	@FieldDescribe("关联目标列表.")
	private List<SiteTargetWi> siteTargetList;

	public List<SiteTargetWi> getSiteTargetList() {
		return siteTargetList;
	}

	public void setSiteTargetList(
			List<SiteTargetWi> siteTargetList) {
		this.siteTargetList = siteTargetList;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}

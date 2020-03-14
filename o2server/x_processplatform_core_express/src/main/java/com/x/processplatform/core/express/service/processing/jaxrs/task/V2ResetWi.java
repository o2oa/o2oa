package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class V2ResetWi extends GsonPropertyObject {

	@FieldDescribe("路由名称")
	private String routeName;

	@FieldDescribe("意见")
	private String opinion;

	@FieldDescribe("重置身份")
	private List<String> identityList;

	@FieldDescribe("保留自身待办.")
	private Boolean keep;

	@FieldDescribe("操作串号")
	private String series;

	public V2ResetWi() {
		this.series = StringTools.uniqueToken();
	}

	public Boolean getKeep() {
		if (null == keep) {
			keep = false;
		}
		return keep;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public void setKeep(Boolean keep) {
		this.keep = keep;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

}
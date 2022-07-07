package com.x.processplatform.core.express.assemble.surface.jaxrs.data;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFetchWithJobWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8049393171576534852L;

	@FieldDescribe("查询路径.")
	@Schema(description = "查询路径.")
	private List<String> pathList;

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

}
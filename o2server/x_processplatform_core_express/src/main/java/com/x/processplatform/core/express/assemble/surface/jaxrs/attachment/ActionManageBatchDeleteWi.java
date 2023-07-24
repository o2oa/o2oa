package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionManageBatchDeleteWi extends GsonPropertyObject {

	private static final long serialVersionUID = -1562006729508038976L;

	@FieldDescribe("待删除附件列表.")
	@Schema(description = "待删除附件列表.")
	private List<String> idList;

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}
}

package com.x.processplatform.core.express.service.processing.jaxrs.task;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.express.service.processing.jaxrs.work.V2AddManualTaskIdentityMatrixWi.Option;

public class V2AddWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	@FieldDescribe("操作")
	private List<Option> optionList;

	@FieldDescribe("是否删除指定待办身份")
	private Boolean remove;

	public List<Option> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<Option> optionList) {
		this.optionList = optionList;
	}

	public Boolean getRemove() {
		return remove;
	}

	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

}
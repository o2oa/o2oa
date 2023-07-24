package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2AddManualTaskIdentityMatrixWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8631082471633729236L;

	@FieldDescribe("身份")
	private String identity;

	@FieldDescribe("操作")
	private List<Option> optionList;

	@FieldDescribe("是否删除指定待办身份")
	private Boolean remove;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

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

	public static class Option {

		@FieldDescribe("位置,before,after,top,bottom,extend")
		private String position;

		@FieldDescribe("身份")
		private List<String> identityList;

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

}
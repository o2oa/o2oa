package com.x.organization.core.express.identity;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

abstract class BaseAction {

	protected static Class<?> applicationClass = x_organization_assemble_express.class;

	static class WoIdentityAbstract extends GsonPropertyObject {

		@FieldDescribe("身份识别名")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

}
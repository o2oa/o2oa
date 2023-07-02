package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.IdentityPersonPair;

abstract class BaseAction {

	protected static Class<?> applicationClass = x_organization_assemble_express.class;

	static class WoPersonListAbstract extends GsonPropertyObject {

		private static final long serialVersionUID = -7355763734963073999L;
		@FieldDescribe("个人识别名")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	static class WoIdentityPersonPairListAbstract extends GsonPropertyObject {

		private static final long serialVersionUID = -1351857861060358701L;

		@FieldDescribe("身份人员匹配对")
		private List<IdentityPersonPair> identityPersonPairList = new ArrayList<>();

		public List<IdentityPersonPair> getIdentityPersonPairList() {
			return identityPersonPairList;
		}

		public void setIdentityPersonPairList(List<IdentityPersonPair> identityPersonPairList) {
			this.identityPersonPairList = identityPersonPairList;
		}

	}

}

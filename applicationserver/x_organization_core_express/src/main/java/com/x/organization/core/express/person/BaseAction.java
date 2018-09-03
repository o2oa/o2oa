package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

abstract class BaseAction {

	protected static Class<?> applicationClass = x_organization_assemble_express.class;

	static class WoPersonListAbstract extends GsonPropertyObject {

		@FieldDescribe("个人识别名")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

}

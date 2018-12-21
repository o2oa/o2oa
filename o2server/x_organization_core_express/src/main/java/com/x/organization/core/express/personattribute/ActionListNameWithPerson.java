package com.x.organization.core.express.personattribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListNameWithPerson extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getPersonList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "personattribute/list/name/person", wi)
				.getData(Wo.class);
		return wo.getNameList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("个人属性名称")
		private List<String> nameList = new ArrayList<>();

		public List<String> getNameList() {
			return nameList;
		}

		public void setNameList(List<String> nameList) {
			this.nameList = nameList;
		}

	}
}

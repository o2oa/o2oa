package com.x.organization.core.express.person;

import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListWithPersonAttribute extends BaseAction {

	public static List<String> execute(AbstractContext context, String name, String attribute) throws Exception {
		Wi wi = new Wi();
		wi.setName(name);
		wi.setAttribute(attribute);
		Wo wo = context.applications().postQuery(applicationClass, "person/list/personattribute", wi).getData(Wo.class);
		return wo.getPersonList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人属性名称")
		private String name;
		@FieldDescribe("个人属性值")
		private String attribute;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAttribute() {
			return attribute;
		}

		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}

	}

	public static class Wo extends WoPersonListAbstract {

	}
}

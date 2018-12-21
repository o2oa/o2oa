package com.x.organization.core.express.personattribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionSetWithPersonWithName extends BaseAction {

	public static boolean execute(AbstractContext context, String person, String name, Collection<String> attributeList)
			throws Exception {
		Wi wi = new Wi();
		wi.setPerson(person);
		wi.setName(name);
		wi.getAttributeList().addAll(attributeList);
		Wo wo = context.applications().postQuery(applicationClass, "personattribute/set/person/name", wi)
				.getData(Wo.class);
		return wo.getValue();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("属性值")
		List<String> attributeList = new ArrayList<>();

		@FieldDescribe("属性名称")
		private String name;

		@FieldDescribe("个人")
		private String person;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public List<String> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<String> attributeList) {
			this.attributeList = attributeList;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Wo extends WrapBoolean {
	}
}

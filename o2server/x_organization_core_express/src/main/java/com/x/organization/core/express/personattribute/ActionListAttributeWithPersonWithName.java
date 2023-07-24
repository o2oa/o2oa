package com.x.organization.core.express.personattribute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListAttributeWithPersonWithName extends BaseAction {

	public static List<String> execute(AbstractContext context, String person, String name) throws Exception {
		Wi wi = new Wi();
		if (StringUtils.isEmpty(person) || StringUtils.isEmpty(name)) {
			return new ArrayList<>();
		}
		wi.setPerson(person);
		wi.setName(name);
		Wo wo = context.applications().postQuery(applicationClass, "personattribute/list/attribute/person/name", wi)
				.getData(Wo.class);
		return wo.getAttributeList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private String person;

		@FieldDescribe("个人属性名称")
		private String name;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private List<String> attributeList = new ArrayList<>();

		public List<String> getAttributeList() {
			return attributeList;
		}

		public void setAttributeList(List<String> attributeList) {
			this.attributeList = attributeList;
		}
	}
}
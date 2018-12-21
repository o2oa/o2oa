package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionHasRole extends BaseAction {

	public static Boolean execute(AbstractContext context, String person, Collection<String> roleList) throws Exception {
		Wi wi = new Wi();
		wi.setPerson(person);
		wi.getRoleList().addAll(roleList);
		Wo wo = context.applications().postQuery(applicationClass, "person/has/role", wi).getData(Wo.class);
		return wo.getValue();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private String person;

		@FieldDescribe("角色")
		private List<String> roleList = new ArrayList<>();

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

	}

	public static class Wo extends WrapBoolean {

	}
}

package com.x.organization.core.express.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionHasRole extends BaseAction {

	public static Boolean execute(AbstractContext context, String group, Collection<String> roleList)
			throws Exception {
		Wi wi = new Wi();
		wi.setGroup(group);
		wi.getRoleList().addAll(roleList);
		Wo wo = context.applications().postQuery(applicationClass, "group/has/role", wi).getData(Wo.class);
		return wo.getValue();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("群组")
		private String group;

		@FieldDescribe("角色")
		private List<String> roleList = new ArrayList<>();

	 

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

	}

	public static class Wo extends WrapBoolean {

	}
}

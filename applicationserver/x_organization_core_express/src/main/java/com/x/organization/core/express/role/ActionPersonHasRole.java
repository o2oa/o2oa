package com.x.organization.core.express.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionPersonHasRole extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getRoleList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "role/list", wi).getData(Wo.class);
		return wo.getRoleList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("角色")
		private List<String> roleList = new ArrayList<>();

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

	}

	public static class Wo extends WoRoleAbstract {
	}
}

package com.x.organization.core.express.unitduty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListNameWithIdentity extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getIdentityList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "unitduty/list/name/identity", wi).getData(Wo.class);
		return wo.getNameList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("组织职务名称")
		private List<String> nameList = new ArrayList<>();

		public List<String> getNameList() {
			return nameList;
		}

		public void setNameList(List<String> nameList) {
			this.nameList = nameList;
		}

	}
}

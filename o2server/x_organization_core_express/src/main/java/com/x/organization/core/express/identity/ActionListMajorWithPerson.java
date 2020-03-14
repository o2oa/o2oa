package com.x.organization.core.express.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListMajorWithPerson extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getPersonList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "identity/list/major/person", wi).getData(Wo.class);
		return wo.getIdentityList();
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

	public static class Wo extends WoIdentityAbstract {
	}
}

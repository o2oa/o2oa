package com.x.organization.core.express.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Identity;

class ActionListMajorWithPersonObject extends BaseAction {

	public static List<? extends Identity> execute(AbstractContext context, Collection<String> people)
			throws Exception {
		if (null == people || people.isEmpty()) {
			return new ArrayList<Wo>();
		}
		Wi wi = new Wi();
		wi.getPersonList().addAll(people);
		List<Wo> wos = context.applications().postQuery(applicationClass, "identity/list/major/person/object", wi)
				.getDataAsList(Wo.class);
		return wos;
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

	public static class Wo extends Identity {

	}
}
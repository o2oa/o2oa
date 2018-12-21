package com.x.organization.core.express.identity;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Identity;

class ActionGetWithPersonWithUnitObject extends BaseAction {

	public static Identity execute(AbstractContext context, String unit, String person) throws Exception {
		Wi wi = new Wi();
		wi.getUnitList().add(unit);
		wi.getPersonList().add(person);
		List<Wo> wos = context.applications().postQuery(applicationClass, "identity/list/person/unit/object", wi)
				.getDataAsList(Wo.class);
		if (wos.isEmpty()) {
			return null;
		} else {
			return wos.get(0);
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends Identity {

	}
}
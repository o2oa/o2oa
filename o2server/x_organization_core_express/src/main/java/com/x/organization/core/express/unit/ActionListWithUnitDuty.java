package com.x.organization.core.express.unit;

import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListWithUnitDuty extends BaseAction {

	public static List<String> execute(AbstractContext context, String name, String identity) throws Exception {
		Wi wi = new Wi();
		wi.setName(name);
		wi.setIdentity(identity);
		Wo wo = context.applications().postQuery(applicationClass, "unit/list/unitduty", wi).getData(Wo.class);
		return wo.getUnitList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织职务名称")
		private String name;
		@FieldDescribe("组织职务身份")
		private String identity;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

	}

	public static class Wo extends WoUnitListAbstract {

	}
}

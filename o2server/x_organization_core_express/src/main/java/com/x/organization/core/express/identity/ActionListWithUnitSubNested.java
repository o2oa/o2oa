package com.x.organization.core.express.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListWithUnitSubNested extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getUnitList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "identity/list/unit/sub/nested", wi)
				.getData(Wo.class);
		return wo.getIdentityList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

	}

	public static class Wo extends WoIdentityAbstract {
	}
}

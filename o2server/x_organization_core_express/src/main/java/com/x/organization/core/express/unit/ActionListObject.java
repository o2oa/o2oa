package com.x.organization.core.express.unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Unit;

class ActionListObject extends BaseAction {

	public static List<? extends Unit> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getUnitList().addAll(collection);
		}
		List<Wo> wos = context.applications().postQuery(applicationClass, "unit/list/object", wi)
				.getDataAsList(Wo.class);
		return wos;
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

	public static class Wo extends Unit {

	}
}
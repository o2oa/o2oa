package com.x.organization.core.express.unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Unit;

class ActionListWithLevelObject extends BaseAction {

	public static List<Wo> execute(AbstractContext context, Collection<Integer> collection) throws Exception {
		Wi wi = new Wi();
		List<Wo> wos = new ArrayList<>();
		if (null != collection) {
			wi.getLevelList().addAll(collection);
			wos = context.applications().postQuery(applicationClass, "unit/list/level/object", wi)
					.getDataAsList(Wo.class);
		}
		return wos;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织层级")
		private List<Integer> levelList = new ArrayList<>();

		public List<Integer> getLevelList() {
			return levelList;
		}

		public void setLevelList(List<Integer> levelList) {
			this.levelList = levelList;
		}

	}

	public static class Wo extends Unit {

	}
}

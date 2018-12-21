package com.x.organization.core.express.person;

import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListLoginRecent extends BaseAction {

	public static List<String> execute(AbstractContext context, Integer count) throws Exception {
		Wi wi = new Wi();
		Wo wo = new Wo();
		if (null != count) {
			wi.setCount(count);
			wo = context.applications().postQuery(applicationClass, "person/list/login/recent", wi).getData(Wo.class);
		}
		return wo.getPersonList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("数量")
		private Integer count;

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}

	public static class Wo extends WoPersonListAbstract {
	}
}

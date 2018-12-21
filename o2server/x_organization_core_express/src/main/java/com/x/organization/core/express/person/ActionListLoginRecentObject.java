package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Person;

class ActionListLoginRecentObject extends BaseAction {

	public static List<? extends Person> execute(AbstractContext context, Integer count) throws Exception {
		Wi wi = new Wi();
		List<Wo> wos = new ArrayList<>();
		if (null != count) {
			wi.setCount(count);
			wos = context.applications().postQuery(applicationClass, "person/list/login/recent/object", wi)
					.getDataAsList(Wo.class);
		}
		return wos;
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

	public static class Wo extends Person {

	}
}
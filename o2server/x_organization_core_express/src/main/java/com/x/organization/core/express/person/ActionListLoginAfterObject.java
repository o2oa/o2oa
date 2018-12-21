package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Person;

class ActionListLoginAfterObject extends BaseAction {

	public static List<? extends Person> execute(AbstractContext context, Date date) throws Exception {
		Wi wi = new Wi();
		List<Wo> wos = new ArrayList<>();
		if (null != date) {
			wi.setDate(date);
			wos = context.applications().postQuery(applicationClass, "person/list/login/after/object", wi)
					.getDataAsList(Wo.class);
		}
		return wos;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("截至日期")
		private Date date;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

	public static class Wo extends Person {

	}
}
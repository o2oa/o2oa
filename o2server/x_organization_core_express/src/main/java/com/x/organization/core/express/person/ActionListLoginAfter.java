package com.x.organization.core.express.person;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListLoginAfter extends BaseAction {

	public static List<String> execute(AbstractContext context, Date date) throws Exception {
		Wi wi = new Wi();
		Wo wo = new Wo();
		if (null != date) {
			wi.setDate(date);
			wo = context.applications().postQuery(applicationClass, "person/list/login/after", wi).getData(Wo.class);
		}
		return wo.getPersonList();
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

	public static class Wo extends WoPersonListAbstract {
	}
}

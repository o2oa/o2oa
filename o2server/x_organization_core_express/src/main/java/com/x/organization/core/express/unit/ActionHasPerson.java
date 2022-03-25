package com.x.organization.core.express.unit;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionHasPerson extends BaseAction {

	public static Boolean execute(AbstractContext context, String person,String unit,Boolean recursive) throws Exception {
		Wi wi = new Wi();
		wi.setPerson(person);
		wi.setUnit(unit);
		wi.setRecursive(recursive);
		Wo wo = context.applications().postQuery(applicationClass, "unit/check/unit/has/person", wi).getData(Wo.class);
		if (null == wo) {
			return false;
		}
		return wo.getValue();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("*用户")
		private String person;

		@FieldDescribe("*组织")
		private String unit;

		@FieldDescribe("*是否递归查找组织(true|false)")
		private Boolean recursive = true;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public Boolean getRecursive() {
			return recursive;
		}

		public void setRecursive(Boolean recursive) {
			this.recursive = recursive;
		}

	}

	public static class Wo extends WrapBoolean {
	}
}

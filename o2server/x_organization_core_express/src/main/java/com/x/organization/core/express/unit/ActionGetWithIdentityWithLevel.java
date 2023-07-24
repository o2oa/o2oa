package com.x.organization.core.express.unit;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionGetWithIdentityWithLevel extends BaseAction {

	public static String execute(AbstractContext context, String identity, Integer level) throws Exception {
		Wi wi = new Wi();
		wi.setIdentity(identity);
		wi.setLevel(level);
		Wo wo = context.applications().postQuery(applicationClass, "unit/identity/level", wi).getData(Wo.class);
		if (null == wo) {
			return "";
		}
		return wo.getUnit();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private String identity;

		@FieldDescribe("组织级别")
		private Integer level;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public Integer getLevel() {
			return level;
		}

		public void setLevel(Integer level) {
			this.level = level;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("组织")
		private String unit;

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}
}

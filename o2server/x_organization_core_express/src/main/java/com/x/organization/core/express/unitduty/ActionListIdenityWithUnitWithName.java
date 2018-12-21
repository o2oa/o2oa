package com.x.organization.core.express.unitduty;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListIdenityWithUnitWithName extends BaseAction {

	public static List<String> execute(AbstractContext context, String unit, String name) throws Exception {
		Wi wi = new Wi();
		if (StringUtils.isEmpty(unit) || StringUtils.isEmpty(name)) {
			return new ArrayList<>();
		}
		wi.setUnit(unit);
		wi.setName(name);
		Wo wo = context.applications().postQuery(applicationClass, "unitduty/list/identity/unit/name", wi)
				.getData(Wo.class);
		return wo.getIdentityList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("组织")
		private String unit;

		@FieldDescribe("组织职务名称")
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}
}
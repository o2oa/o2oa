package com.x.organization.core.express.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

class ActionGetWithPersonWithUnit extends BaseAction {

	public static String execute(AbstractContext context, String unit, String person) throws Exception {
		if (StringUtils.isEmpty(unit) || StringUtils.isEmpty(person)) {
			return "";
		}
		Wi wi = new Wi();
		wi.getUnitList().add(unit);
		wi.getPersonList().add(person);
		Wo wo = context.applications().postQuery(applicationClass, "identity/list/unit/person", wi).getData(Wo.class);
		if (ListTools.isEmpty(wo.getIdentityList())) {
			return "";
		} else {
			return wo.getIdentityList().get(0);
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

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

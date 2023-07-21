package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionList extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection, Boolean useNameFind) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getPersonList().addAll(collection);
		}
		wi.setUseNameFind(useNameFind);
		Wo wo = context.applications().postQuery(applicationClass, "person/list", wi).getData(Wo.class);
		return wo.getPersonList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> personList = new ArrayList<>();

		@FieldDescribe("是否需要根据名称查找，默认false")
		private Boolean useNameFind = false;

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public Boolean getUseNameFind() {
			return useNameFind;
		}

		public void setUseNameFind(Boolean useNameFind) {
			this.useNameFind = useNameFind;
		}
	}

	public static class Wo extends WoPersonListAbstract {
	}
}

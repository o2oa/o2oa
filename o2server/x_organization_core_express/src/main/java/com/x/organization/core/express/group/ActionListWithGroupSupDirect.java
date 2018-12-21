package com.x.organization.core.express.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListWithGroupSupDirect extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getGroupList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "group/list/group/sup/direct", wi).getData(Wo.class);
		return wo.getGroupList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("群组")
		private List<String> groupList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

	}

	public static class Wo extends WoGroupAbstract {
	}
}

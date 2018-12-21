package com.x.organization.core.express.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.Group;

class ActionListObject extends BaseAction {

	public static List<? extends Group> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getGroupList().addAll(collection);
		}
		List<Wo> wos = context.applications().postQuery(applicationClass, "group/list/object", wi)
				.getDataAsList(Wo.class);
		return wos;
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

	public static class Wo extends Group {

	}
}
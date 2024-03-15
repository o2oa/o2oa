package com.x.organization.core.express.distinguishedname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionList extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getDistinguishedNameList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "distinguishedname/list", wi).getData(Wo.class);
		return wo.getDistinguishedNameList();
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 2919655201914964929L;

		@FieldDescribe("组织专有标识.")
		private List<String> distinguishedNameList = new ArrayList<>();

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -5601967828535997709L;

		@FieldDescribe("组织专有标识.")
		private List<String> distinguishedNameList = new ArrayList<>();

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}

	}
}

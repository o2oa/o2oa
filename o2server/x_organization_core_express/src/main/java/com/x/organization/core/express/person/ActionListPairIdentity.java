package com.x.organization.core.express.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.IdentityPersonPair;

class ActionListPairIdentity extends BaseAction {

	public static List<IdentityPersonPair> execute(AbstractContext context, Collection<String> collection)
			throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getIdentityList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "person/list/pair/identity", wi).getData(Wo.class);
		return wo.getIdentityPersonPairList();
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -3026913509939491119L;

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

	}

	public static class Wo extends WoIdentityPersonPairListAbstract {

		private static final long serialVersionUID = 8008864406475798384L;

	}
}

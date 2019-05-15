package com.x.organization.core.express.trust;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Trust;

class ActionListWithIdentityObject extends BaseAction {

	public static List<Trust> execute(AbstractContext context, String application, String process,
			Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		List<Trust> wos = new ArrayList<>();
		if ((null != collection) && (!collection.isEmpty())) {
			wi.getIdentityList().addAll(collection);
			wos = context.applications().postQuery(applicationClass, "trust/list/identity/object", wi)
					.getDataAsList(Trust.class);
		}
		return wos;
	}

	public static class Wi extends WoAbstract {

	}

}
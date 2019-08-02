package com.x.organization.core.express.empower;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Empower;

class ActionListWithIdentityObject extends BaseAction {

	public static List<Empower> execute(AbstractContext context, String application, String process,
			Collection<String> collection) throws Exception {
		Wi wi = new Wi();
		List<Empower> wos = new ArrayList<>();
		if ((null != collection) && (!collection.isEmpty())) {
			wi.getIdentityList().addAll(collection);
			wos = context.applications().postQuery(applicationClass, "empower/list/identity/object", wi)
					.getDataAsList(Empower.class);
		}
		return wos;
	}

	public static class Wi extends WoAbstract {

	}

}
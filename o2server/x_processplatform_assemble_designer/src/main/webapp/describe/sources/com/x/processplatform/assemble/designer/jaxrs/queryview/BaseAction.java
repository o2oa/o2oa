package com.x.processplatform.assemble.designer.jaxrs.queryview;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.query.Query;

abstract class BaseAction extends StandardJaxrsAction {

	static void transQuery(QueryView queryView) {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson(queryView.getData(), Query.class);
		queryView.setData(gson.toJson(query));
	}

}

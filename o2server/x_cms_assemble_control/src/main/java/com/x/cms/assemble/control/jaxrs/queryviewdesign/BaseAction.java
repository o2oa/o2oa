package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.query.Query;


class BaseAction extends StandardJaxrsAction {
	
	protected void transQuery( QueryView queryView ) {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson( queryView.getData(), Query.class );
		queryView.setData( gson.toJson(query) );
	}
}

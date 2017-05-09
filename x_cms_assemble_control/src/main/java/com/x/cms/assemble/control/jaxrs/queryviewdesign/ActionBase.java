package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.google.gson.Gson;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.query.Query;


class ActionBase extends StandardJaxrsAction {

	protected BeanCopyTools<QueryView, WrapOutQueryView> outCopier = BeanCopyToolsBuilder.create(QueryView.class,
			WrapOutQueryView.class, null, WrapOutQueryView.Excludes);

	protected BeanCopyTools<WrapInQueryView, QueryView> createCopier = BeanCopyToolsBuilder
			.create(WrapInQueryView.class, QueryView.class, null, WrapInQueryView.CreateExcludes);

	protected BeanCopyTools<WrapInQueryView, QueryView> updateCopier = BeanCopyToolsBuilder
			.create(WrapInQueryView.class, QueryView.class, null, WrapInQueryView.UpdateExcludes);

	protected void transQuery(QueryView queryView) {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson(queryView.getData(), Query.class);
		queryView.setData(gson.toJson(query));
	}
}

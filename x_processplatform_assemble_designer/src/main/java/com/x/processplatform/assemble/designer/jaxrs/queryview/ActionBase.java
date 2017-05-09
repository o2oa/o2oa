package com.x.processplatform.assemble.designer.jaxrs.queryview;

import com.google.gson.Gson;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryView;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryView;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.query.Query;

class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<QueryView, WrapOutQueryView> outCopier = BeanCopyToolsBuilder.create(QueryView.class,
			WrapOutQueryView.class, null, WrapOutQueryView.Excludes);

	static BeanCopyTools<WrapInQueryView, QueryView> createCopier = BeanCopyToolsBuilder.create(WrapInQueryView.class,
			QueryView.class, null, WrapInQueryView.CreateExcludes);

	static BeanCopyTools<WrapInQueryView, QueryView> updateCopier = BeanCopyToolsBuilder.create(WrapInQueryView.class,
			QueryView.class, null, WrapInQueryView.UpdateExcludes);

	static void transQuery(QueryView queryView) {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson(queryView.getData(), Query.class);
		queryView.setData(gson.toJson(query));
	}

}

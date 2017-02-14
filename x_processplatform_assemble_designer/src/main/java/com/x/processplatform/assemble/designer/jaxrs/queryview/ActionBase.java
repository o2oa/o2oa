package com.x.processplatform.assemble.designer.jaxrs.queryview;

import com.google.gson.Gson;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryView;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryView;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.query.Query;

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
	// protected void updateRestrict(QueryView queryView) throws Exception {
	// queryView.setRestrictDateRangeEntry(null);
	// queryView.setRestrictWhereEntry(null);
	// queryView.setRestrictFilterEntry(null);
	// if (StringUtils.isNotEmpty(queryView.getQuery())) {
	// Gson gson = XGsonBuilder.instance();
	// Query query = XGsonBuilder.instance().fromJson(queryView.getQuery(),
	// Query.class);
	// if (null != query.getRestrictDateRangeEntry()) {
	// queryView.setRestrictDateRangeEntry(gson.toJson(query.getRestrictDateRangeEntry()));
	// }
	// if (null != query.getRestrictFilterEntryList()) {
	// queryView.setRestrictFilterEntry(gson.toJson(query.getRestrictFilterEntryList()));
	// }
	// if (null != query.getRestrictWhereEntry()) {
	// queryView.setRestrictWhereEntry(gson.toJson(query.getRestrictWhereEntry()));
	// }
	// }
	// }

}

package com.x.cms.assemble.control.jaxrs.queryview;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.query.Query;

public class ActionExecute extends ActionBase {

	public ActionResult<Query> execute(EffectivePerson effectivePerson, String flag, WrapInQueryViewExecute wrapIn)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Query> result = new ActionResult<>();
			Business business = new Business(emc);
			QueryView queryView = business.queryViewFactory().pick(flag, ExceptionWhen.not_found);
			if (!business.queryViewFactory().allowRead(effectivePerson, queryView)) {
				throw new Exception("insufficient permissions.");
			}
			Query query = this.concrete(queryView, wrapIn);
			query.query();
			result.setData(query);
			return result;
		}
	}

	private Query concrete(QueryView queryView, WrapInQueryViewExecute wrapIn) throws Exception {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson(queryView.getData(), Query.class);
		query.setFilterEntryList(wrapIn.getFilterEntryList());
		query.setWhereEntry(wrapIn.getWhereEntry());
		return query;
	}

}

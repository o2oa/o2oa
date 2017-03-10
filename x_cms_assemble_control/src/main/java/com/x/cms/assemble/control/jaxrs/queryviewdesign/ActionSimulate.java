package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.query.Query;
import com.x.cms.core.entity.query.SelectEntry;


class ActionSimulate extends ActionBase {

	public ActionResult<Query> execute(EffectivePerson effectivePerson, String flag, Query wrapIn) throws Exception {
		/* 前台通过wrapIn将Query的可选择部分FilterEntryList和WhereEntry进行输入 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Query> result = new ActionResult<>();
			QueryView queryView = emc.flag(flag, QueryView.class, ExceptionWhen.not_found, false, QueryView.FLAGS);
			Query query = this.concrete(queryView);
			if (null != wrapIn) {
				if (ListTools.isNotEmpty(wrapIn.getFilterEntryList())) {
					query.setFilterEntryList(wrapIn.getFilterEntryList());
				}
				if (this.selectEntryListAvailable( wrapIn )) {
					query.setWhereEntry( wrapIn.getWhereEntry() );
				}
			}
			query.query();
			result.setData(query);
			return result;
		}
	}

	private Boolean selectEntryListAvailable(Query wrapIn) {
		for (SelectEntry o : ListTools.nullToEmpty(wrapIn.getSelectEntryList())) {
			if (o.available()) {
				return true;
			}
		}
		return false;
	}

	private Query concrete(QueryView queryView) throws Exception {
		Gson gson = XGsonBuilder.instance();
		Query query = gson.fromJson(queryView.getData(), Query.class);
		return query;
	}

}
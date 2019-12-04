package com.x.query.assemble.designer.jaxrs.output;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.wrap.*;

import net.sf.ehcache.Element;

class ActionSelect extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String queryFlag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Query query = emc.flag(queryFlag, Query.class );
			if (null == query) {
				throw new ExceptionQueryNotExist(queryFlag);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			WrapQuery wrapQuery = this.get(business, query, wi);
			CacheObject cacheObject = new CacheObject();
			cacheObject.setName(query.getName());
			cacheObject.setQuery(wrapQuery);
			String flag = StringTools.uniqueToken();
			this.cache.put(new Element(flag, cacheObject));
			Wo wo = XGsonBuilder.convert(wrapQuery, Wo.class);
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private WrapQuery get(Business business, Query query, Wi wi) throws Exception {
		WrapQuery wo = WrapQuery.outCopier.copy(query);
		wo.setViewList(WrapView.outCopier.copy(business.entityManagerContainer().list(View.class, wi.listViewId())));
		wo.setStatList(WrapStat.outCopier.copy(business.entityManagerContainer().list(Stat.class, wi.listStatId())));
		wo.setRevealList(
				WrapReveal.outCopier.copy(business.entityManagerContainer().list(Reveal.class, wi.listRevealId())));
		wo.setTableList(WrapTable.outCopier.copy(business.entityManagerContainer().list(Table.class, wi.listTableId())));
		wo.setStatementList(WrapStatement.outCopier.copy(business.entityManagerContainer().list(Statement.class, wi.listStatementId())));
		return wo;
	}

	// private List<WrapView> listView(Business business, Query query, Wi wi) throws
	// Exception {
	// List<WrapView> wos = new ArrayList<>();
	// for (WrapView wrap : wi.getViewList()) {
	// View o = business.entityManagerContainer().find(wrap.getId(), View.class);
	// if (null == o) {
	// throw new ExceptionViewNotExist(wrap.getId());
	// }
	// wos.add(WrapView.outCopier.copy(o));
	// }
	//
	// // List<String> ids = business.view().listWithQuery(query.getId());
	// // if (!StringUtils.equals("*", wi.getViewList().get(0))) {
	// // ids = ListUtils.intersection(ids, wi.getViewList());
	// // }
	// // for (String id : ListTools.trim(ids, true, true)) {
	// // View o = business.entityManagerContainer().find(id, View.class);
	// // if (null == o) {
	// // throw new ExceptionViewNotExist(id);
	// // }
	// // wos.add(WrapView.outCopier.copy(o));
	// // }
	// return wos;
	// }
	//
	// private List<WrapStat> listStat(Business business, Query query, Wi wi) throws
	// Exception {
	// List<WrapStat> wos = new ArrayList<>();
	// for (String id : ListTools.trim(ids, true, true)) {
	// Stat o = business.entityManagerContainer().find(id, Stat.class);
	// if (null == o) {
	// throw new ExceptionStatNotExist(id);
	// }
	// wos.add(WrapStat.outCopier.copy(o));
	// }
	// return wos;
	// }
	//
	// private List<WrapReveal> listReveal(Business business, Query query, Wi wi)
	// throws Exception {
	// List<WrapReveal> wos = new ArrayList<>();
	// if (ListTools.isEmpty(wi.getRevealList())) {
	// return wos;
	// }
	// List<String> ids = business.reveal().listWithQuery(query.getId());
	// if (!StringUtils.equals("*", wi.getRevealList().get(0))) {
	// ids = ListUtils.intersection(ids, wi.getRevealList());
	// }
	// for (String id : ListTools.trim(ids, true, true)) {
	// Reveal o = business.entityManagerContainer().find(id, Reveal.class);
	// if (null == o) {
	// throw new ExceptionRevealNotExist(id);
	// }
	// wos.add(WrapReveal.outCopier.copy(o));
	// }
	// return wos;
	// }

	public static class Wi extends WrapQuery {

		private static final long serialVersionUID = -5670907699997607096L;

	}

	public static class Wo extends WrapQuery {

		private static final long serialVersionUID = -1130848016754973977L;
		@FieldDescribe("返回标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

}
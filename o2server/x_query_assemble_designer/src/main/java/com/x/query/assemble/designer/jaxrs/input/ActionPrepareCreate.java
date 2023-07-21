package com.x.query.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.wrap.WrapImportModel;
import com.x.query.core.entity.wrap.WrapQuery;
import com.x.query.core.entity.wrap.WrapStat;
import com.x.query.core.entity.wrap.WrapStatement;
import com.x.query.core.entity.wrap.WrapTable;
import com.x.query.core.entity.wrap.WrapView;

class ActionPrepareCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = this.adjustForCreate(business, wi);
			result.setData(wos);

			return result;
		}
	}

	private List<Wo> adjustForCreate(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Query exist = business.entityManagerContainer().find(wi.getId(), Query.class);
		if (null != exist) {
			wos.add(new Wo(wi.getId(), JpaObject.createId()));
		}
		for (WrapView wrap : wi.getViewList()) {
			View _o = business.entityManagerContainer().find(wrap.getId(), View.class);
			if (null != _o) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapStat wrap : wi.getStatList()) {
			Stat _o = business.entityManagerContainer().find(wrap.getId(), Stat.class);
			if (null != _o) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapTable wrap : wi.getTableList()) {
			Table _o = business.entityManagerContainer().find(wrap.getId(), Table.class);
			if (null != _o) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapStatement wrap : wi.getStatementList()) {
			Statement _o = business.entityManagerContainer().find(wrap.getId(), Statement.class);
			if (null != _o) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapImportModel wrap : wi.getImportModelList()) {
			ImportModel _o = business.entityManagerContainer().find(wrap.getId(), ImportModel.class);
			if (null != _o) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		return wos;
	}

	public static class Wi extends WrapQuery {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WrapPair {
		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}

	}

}

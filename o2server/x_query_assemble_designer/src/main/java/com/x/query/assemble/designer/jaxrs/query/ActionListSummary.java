package com.x.query.assemble.designer.jaxrs.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;

class ActionListSummary extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListSummary.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = this.list(business, effectivePerson);
			List<String> ids = ListTools.extractProperty(wos, Query.id_FIELDNAME, String.class, true, true);
			List<WoView> woViews = emc.fetchIn(View.class, WoView.copier, View.query_FIELDNAME, ids);
			List<WoStat> woStats = emc.fetchIn(Stat.class, WoStat.copier, View.query_FIELDNAME, ids);
			ListTools.groupStick(wos, woViews, Query.id_FIELDNAME, View.query_FIELDNAME, "woViewList");
			ListTools.groupStick(wos, woStats, Query.id_FIELDNAME, Stat.query_FIELDNAME, "woStatList");
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson) throws Exception {
		List<Wo> wos = null;
		if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.QueryManager, OrganizationDefinition.QueryCreator))) {
			wos = business.entityManagerContainer().fetchEuqalOrIsMember(Query.class, Wo.copier,
					Query.creatorPerson_FIELDNAME, effectivePerson.getDistinguishedName(),
					Query.controllerList_FIELDNAME, effectivePerson.getDistinguishedName());
		} else {
			wos = business.entityManagerContainer().fetchAll(Query.class, Wo.copier);
		}
		return wos;
	}

	public static class Wo extends Query {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Query, Wo> copier = WrapCopierFactory.wo(
				Query.class, Wo.class, ListTools.toList(JpaObject.id_FIELDNAME, Query.name_FIELDNAME,
						Query.description_FIELDNAME, Query.queryCategory_FIELDNAME, JpaObject.updateTime_FIELDNAME),
				null);

		@FieldDescribe("视图对象")
		private List<WoView> woViewList = new ArrayList<>();

		@FieldDescribe("统计对象")
		private List<WoStat> woStatList = new ArrayList<>();

		public List<WoView> getWoViewList() {
			return woViewList;
		}

		public void setWoViewList(List<WoView> woViewList) {
			this.woViewList = woViewList;
		}

		public List<WoStat> getWoStatList() {
			return woStatList;
		}

		public void setWoStatList(List<WoStat> woStatList) {
			this.woStatList = woStatList;
		}

	}

	public static class WoView extends View {

		private static final long serialVersionUID = 1439909268641168987L;

		static WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class,
				ListTools.toList(JpaObject.id_FIELDNAME, View.name_FIELDNAME, View.query_FIELDNAME,
						JpaObject.updateTime_FIELDNAME),
				null);

	}

	public static class WoStat extends Stat {

		private static final long serialVersionUID = 1513668573527819003L;

		static WrapCopier<Stat, WoStat> copier = WrapCopierFactory.wo(Stat.class, WoStat.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Stat.name_FIELDNAME, Stat.query_FIELDNAME,
						JpaObject.updateTime_FIELDNAME),
				null);
	}

}
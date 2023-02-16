package com.x.query.assemble.designer.jaxrs.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
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

class ActionListAll extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListAll.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = this.list(business, effectivePerson);
			List<String> ids = ListTools.extractProperty(wos, Query.id_FIELDNAME, String.class, true, true);
			List<WoView> woViews = emc.fetchIn(View.class, WoView.copier, View.query_FIELDNAME, ids);
			List<WoStat> woStats = emc.fetchIn(Stat.class, WoStat.copier, View.query_FIELDNAME, ids);
			ListTools.groupStick(wos, woViews, Query.id_FIELDNAME, View.query_FIELDNAME, "viewList");
			ListTools.groupStick(wos, woStats, Query.id_FIELDNAME, Stat.query_FIELDNAME, "statList");
			wos = business.query().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson) throws Exception {
		List<Wo> wos = null;
		if ((!effectivePerson.isSecurityManager()) && (!effectivePerson.isManager())
				&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.QueryManager,
						OrganizationDefinition.QueryCreator))) {
			wos = business.entityManagerContainer().fetchEuqalOrIsMember(Query.class, Wo.copier,
					Query.creatorPerson_FIELDNAME, effectivePerson.getDistinguishedName(),
					Query.controllerList_FIELDNAME, effectivePerson.getDistinguishedName());
		} else {
			wos = business.entityManagerContainer().fetchAll(Query.class, Wo.copier);

		}
		return wos;
	}

	public static class Wo extends Query {

		private static final long serialVersionUID = 2886873983211744188L;

		static WrapCopier<Query, Wo> copier = WrapCopierFactory.wo(Query.class, Wo.class,
				JpaObject.singularAttributeField(Query.class, true, false), null);

		private List<WoView> viewList = new ArrayList<>();

		private List<WoStat> statList = new ArrayList<>();

		public List<WoView> getViewList() {
			return viewList;
		}

		public void setViewList(List<WoView> viewList) {
			this.viewList = viewList;
		}

		public List<WoStat> getStatList() {
			return statList;
		}

		public void setStatList(List<WoStat> statList) {
			this.statList = statList;
		}

	}

	public static class WoView extends View {

		private static final long serialVersionUID = 1439909268641168987L;

		static WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class, ListTools
				.toList(View.id_FIELDNAME, View.name_FIELDNAME, View.query_FIELDNAME, View.updateTime_FIELDNAME), null);

	}

	public static class WoStat extends Stat {

		private static final long serialVersionUID = 1513668573527819003L;

		static WrapCopier<Stat, WoStat> copier = WrapCopierFactory.wo(Stat.class, WoStat.class, ListTools
				.toList(Stat.id_FIELDNAME, Stat.name_FIELDNAME, Stat.query_FIELDNAME, Stat.updateTime_FIELDNAME), null);
	}
}

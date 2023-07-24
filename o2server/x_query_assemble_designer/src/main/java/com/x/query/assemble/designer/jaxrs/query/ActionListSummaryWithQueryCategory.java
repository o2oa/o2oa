package com.x.query.assemble.designer.jaxrs.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.beanutils.PropertyUtils;

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

class ActionListSummaryWithQueryCategory extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListSummaryWithQueryCategory.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String queryCategory) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = this.list(business, effectivePerson, queryCategory);
			List<String> ids = ListTools.extractProperty(wos, Query.id_FIELDNAME, String.class, true, true);
			List<WoView> woViews = emc.fetchIn(View.class, WoView.copier, View.query_FIELDNAME, ids);
			List<WoStat> woStats = emc.fetchIn(Stat.class, WoStat.copier, View.query_FIELDNAME, ids);
			ListTools.groupStick(wos, woViews, Query.id_FIELDNAME, View.query_FIELDNAME, "woViewList");
			ListTools.groupStick(wos, woStats, Query.id_FIELDNAME, Stat.query_FIELDNAME, "woStatList");
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson, String queryCategory) throws Exception {
		List<Wo> list = new ArrayList<>();
		List<String> fields = ListTools.trim(Wo.copier.getCopyFields(), true, true, JpaObject.id_FIELDNAME);
		EntityManager em = business.entityManagerContainer().get(Query.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Query> root = cq.from(Query.class);
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : fields) {
			selections.add(root.get(str));
		}
		Predicate p = cb.equal(root.get(Query.queryCategory_FIELDNAME), Objects.toString(queryCategory, ""));
		if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.QueryManager, OrganizationDefinition.QueryCreator))) {
			p = cb.and(p, cb.or(
					cb.equal(root.get(Query.creatorPerson_FIELDNAME), effectivePerson.getDistinguishedName()),
					cb.isMember(effectivePerson.getDistinguishedName(), root.get(Query.controllerList_FIELDNAME))));
		}
		cq.multiselect(selections).where(p);
		for (Tuple o : em.createQuery(cq).getResultList()) {
			Query q = new Query();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(q, fields.get(i), o.get(selections.get(i)));
			}
			list.add(Wo.copier.copy(q));
		}
		return list;
	}

	public static class Wo extends Query {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Query, Wo> copier = WrapCopierFactory.wo(Query.class, Wo.class,
				ListTools.toList(Query.id_FIELDNAME, Query.name_FIELDNAME, Query.description_FIELDNAME,
						Query.queryCategory_FIELDNAME, Query.updateTime_FIELDNAME),
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

		static WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class, ListTools
				.toList(View.id_FIELDNAME, View.name_FIELDNAME, View.query_FIELDNAME, View.updateTime_FIELDNAME), null);

	}

	public static class WoStat extends Stat {

		private static final long serialVersionUID = 1513668573527819003L;

		static WrapCopier<Stat, WoStat> copier = WrapCopierFactory.wo(Stat.class, WoStat.class, ListTools
				.toList(Stat.id_FIELDNAME, Stat.name_FIELDNAME, Stat.query_FIELDNAME, Stat.updateTime_FIELDNAME), null);
	}

}
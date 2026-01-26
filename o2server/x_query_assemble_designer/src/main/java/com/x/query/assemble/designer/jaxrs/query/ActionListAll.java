package com.x.query.assemble.designer.jaxrs.query;

import com.x.query.core.entity.schema.Table;
import com.x.query.core.entity.schema.Statement;
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
			List<String> ids = ListTools.extractProperty(wos, JpaObject.id_FIELDNAME, String.class, true, true);
			List<WoView> woViews = emc.fetchIn(View.class, WoView.copier, View.query_FIELDNAME, ids);
			List<WoStat> woStats = emc.fetchIn(Stat.class, WoStat.copier, Stat.query_FIELDNAME, ids);
			List<WoTable> woTables = emc.fetchIn(Table.class, WoTable.copier, Table.QUERY_FIELDNAME, ids);
			List<WoStatement> woStatements = emc.fetchIn(Statement.class, WoStatement.copier, Statement.QUERY_FIELDNAME, ids);
			ListTools.groupStick(wos, woViews, JpaObject.id_FIELDNAME, View.query_FIELDNAME, "viewList");
			ListTools.groupStick(wos, woStats, JpaObject.id_FIELDNAME, Stat.query_FIELDNAME, "statList");
			ListTools.groupStick(wos, woTables, JpaObject.id_FIELDNAME, Table.QUERY_FIELDNAME, "tableList");
			ListTools.groupStick(wos, woStatements, JpaObject.id_FIELDNAME, Statement.QUERY_FIELDNAME, "statementList");
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
		private List<WoTable> tableList = new ArrayList<>();
		private List<WoStatement> statementList = new ArrayList<>();

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

		public List<WoStatement> getStatementList() {
			return statementList;
		}

		public void setStatementList(
				List<WoStatement> statementList) {
			this.statementList = statementList;
		}

		public List<WoTable> getTableList() {
			return tableList;
		}

		public void setTableList(
				List<WoTable> tableList) {
			this.tableList = tableList;
		}
	}

	public static class WoView extends View {

		private static final long serialVersionUID = 1439909268641168987L;

		static WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class, ListTools
				.toList(JpaObject.id_FIELDNAME, View.name_FIELDNAME, View.query_FIELDNAME, JpaObject.updateTime_FIELDNAME), null);

	}

	public static class WoStat extends Stat {

		private static final long serialVersionUID = 1513668573527819003L;

		static WrapCopier<Stat, WoStat> copier = WrapCopierFactory.wo(Stat.class, WoStat.class, ListTools
				.toList(JpaObject.id_FIELDNAME, Stat.name_FIELDNAME, Stat.query_FIELDNAME, JpaObject.updateTime_FIELDNAME), null);
	}

	public static class WoTable extends Table {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Table, WoTable> copier = WrapCopierFactory.wo(Table.class, WoTable.class,
				ListTools
						.toList(JpaObject.id_FIELDNAME, Table.NAME_FIELDNAME, Table.QUERY_FIELDNAME, JpaObject.updateTime_FIELDNAME),
				null);
	}

	public static class WoStatement extends Statement {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Statement, WoStatement> copier = WrapCopierFactory.wo(Statement.class, WoStatement.class,
				ListTools
						.toList(JpaObject.id_FIELDNAME, Statement.NAME_FIELDNAME, Statement.QUERY_FIELDNAME, JpaObject.updateTime_FIELDNAME),
				null);
	}
}

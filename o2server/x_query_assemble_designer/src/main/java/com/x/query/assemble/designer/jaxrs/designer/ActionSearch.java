package com.x.query.assemble.designer.jaxrs.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.enums.DesignerType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WiDesigner;
import com.x.base.core.project.jaxrs.WrapDesigner;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionSearch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSearch.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		logger.debug("{}开始数据中心设计搜索，关键字：{}", effectivePerson.getDistinguishedName(), wi.getKeyword());
		if (StringUtils.isBlank(wi.getKeyword())) {
			throw new ExceptionFieldEmpty("keyword");
		}
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> resWos = new ArrayList<>();
		List<CompletableFuture<List<Wo>>> list = new ArrayList<>();
		Map<String, List<String>> designerMap = wi.getAppDesigner();
		List<String> appList = wi.getAppIdList();

		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.view.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.view.toString()))) {
			list.add(searchView(wi, appList, designerMap.get(DesignerType.view.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.table.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.table.toString()))) {
			list.add(searchTable(wi, appList, designerMap.get(DesignerType.table.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.statement.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.statement.toString()))) {
			list.add(searchStatement(wi, appList, designerMap.get(DesignerType.statement.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.stat.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.stat.toString()))) {
			list.add(searchStat(wi, appList, designerMap.get(DesignerType.stat.toString())));
		}

		for (CompletableFuture<List<Wo>> cf : list) {
			if (resWos.size() < 50) {
				resWos.addAll(cf.get(60, TimeUnit.SECONDS));
			}
		}
		if (resWos.size() > 50) {
			resWos = resWos.subList(0, 50);
		}
		result.setData(resWos);
		result.setCount((long) resWos.size());
		return result;
	}

	private CompletableFuture<List<Wo>> searchView(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoView> woViews;
				if (ListTools.isNotEmpty(designerIdList)) {
					woViews = emc.fetchIn(View.class, WoView.copier, View.id_FIELDNAME, designerIdList);
				} else if (ListTools.isNotEmpty(appIdList)) {
					woViews = emc.fetchIn(View.class, WoView.copier, View.query_FIELDNAME, appIdList);
				} else {
					woViews = emc.fetchAll(View.class, WoView.copier);
				}
				for (WoView woView : woViews) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoView.copier.getCopyFields(), woView,
							wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.fetch(woView.getQuery(), Query.class,
								ListTools.toList(Query.id_FIELDNAME, Query.name_FIELDNAME));
						if (query != null) {
							wo.setAppId(query.getId());
							wo.setAppName(query.getName());
						}
						wo.setDesignerId(woView.getId());
						wo.setDesignerName(woView.getName());
						wo.setDesignerType(DesignerType.view.toString());
						wo.setUpdateTime(woView.getUpdateTime());
						wo.setPatternList(map);
						resWos.add(wo);
					}
				}
				woViews.clear();
			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<Wo>> searchTable(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoTable> woTables;
				if (ListTools.isNotEmpty(designerIdList)) {
					woTables = emc.fetchIn(Table.class, WoTable.copier, Table.id_FIELDNAME, designerIdList);
				} else if (ListTools.isNotEmpty(appIdList)) {
					woTables = emc.fetchIn(Table.class, WoTable.copier, Table.QUERY_FIELDNAME, appIdList);
				} else {
					woTables = emc.fetchAll(Table.class, WoTable.copier);
				}
				for (WoTable woTable : woTables) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoTable.copier.getCopyFields(), woTable,
							wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.fetch(woTable.getQuery(), Query.class,
								ListTools.toList(Query.id_FIELDNAME, Query.name_FIELDNAME));
						if (query != null) {
							wo.setAppId(query.getId());
							wo.setAppName(query.getName());
						}
						wo.setDesignerId(woTable.getId());
						wo.setDesignerName(woTable.getName());
						wo.setDesignerType(DesignerType.table.toString());
						wo.setUpdateTime(woTable.getUpdateTime());
						wo.setPatternList(map);
						resWos.add(wo);
					}
				}
				woTables.clear();
				woTables = null;
			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<Wo>> searchStat(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoStat> woStats;
				if (ListTools.isNotEmpty(designerIdList)) {
					woStats = emc.fetchIn(Stat.class, WoStat.copier, Stat.id_FIELDNAME, designerIdList);
				} else if (ListTools.isNotEmpty(appIdList)) {
					woStats = emc.fetchIn(Stat.class, WoStat.copier, Stat.query_FIELDNAME, appIdList);
				} else {
					woStats = emc.fetchAll(Stat.class, WoStat.copier);
				}
				for (WoStat woStat : woStats) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoStat.copier.getCopyFields(), woStat,
							wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.fetch(woStat.getQuery(), Query.class,
								ListTools.toList(Query.id_FIELDNAME, Query.name_FIELDNAME));
						if (query != null) {
							wo.setAppId(query.getId());
							wo.setAppName(query.getName());
						}
						wo.setDesignerId(woStat.getId());
						wo.setDesignerName(woStat.getName());
						wo.setDesignerType(DesignerType.stat.toString());
						wo.setUpdateTime(woStat.getUpdateTime());
						wo.setPatternList(map);
						resWos.add(wo);
					}
				}
				woStats.clear();
				woStats = null;
			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<Wo>> searchStatement(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoStatement> woStatements;
				if (ListTools.isNotEmpty(designerIdList)) {
					woStatements = emc.fetchIn(Statement.class, WoStatement.copier, Statement.id_FIELDNAME,
							designerIdList);
				} else if (ListTools.isNotEmpty(appIdList)) {
					woStatements = emc.fetchIn(Statement.class, WoStatement.copier, Statement.QUERY_FIELDNAME,
							appIdList);
				} else {
					woStatements = emc.fetchAll(Statement.class, WoStatement.copier);
				}
				for (WoStatement woStatement : woStatements) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoStatement.copier.getCopyFields(),
							woStatement, wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(),
							wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.fetch(woStatement.getQuery(), Query.class,
								ListTools.toList(Query.id_FIELDNAME, Query.name_FIELDNAME));
						if (query != null) {
							wo.setAppId(query.getId());
							wo.setAppName(query.getName());
						}
						wo.setDesignerId(woStatement.getId());
						wo.setDesignerName(woStatement.getName());
						wo.setDesignerType(DesignerType.statement.toString());
						wo.setUpdateTime(woStatement.getUpdateTime());
						wo.setPatternList(map);
						resWos.add(wo);
					}
				}
				woStatements.clear();
				woStatements = null;
			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	public static class Wi extends WiDesigner {

		private static final long serialVersionUID = 271382242755613200L;

	}

	public static class Wo extends WrapDesigner {

		private static final long serialVersionUID = 6940502040478135933L;

	}

	public static class WoView extends View {

		private static final long serialVersionUID = 7215796246765954887L;

		static WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class,
				JpaObject.singularAttributeField(View.class, true, false), null);

	}

	public static class WoStat extends Stat {

		private static final long serialVersionUID = 2751258453405875094L;

		static WrapCopier<Stat, WoStat> copier = WrapCopierFactory.wo(Stat.class, WoStat.class,
				JpaObject.singularAttributeField(Stat.class, true, false), null);

	}

	public static class WoTable extends Table {

		private static final long serialVersionUID = -6977626699058281575L;

		static WrapCopier<Table, WoTable> copier = WrapCopierFactory.wo(Table.class, WoTable.class,
				JpaObject.singularAttributeField(Table.class, true, false), null);

	}

	public static class WoStatement extends Statement {

		private static final long serialVersionUID = 371216240335095674L;

		static WrapCopier<Statement, WoStatement> copier = WrapCopierFactory.wo(Statement.class, WoStatement.class,
				JpaObject.singularAttributeField(Statement.class, true, false), null);

	}

}

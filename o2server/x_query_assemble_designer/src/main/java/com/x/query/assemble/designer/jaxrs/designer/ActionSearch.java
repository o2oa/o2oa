package com.x.query.assemble.designer.jaxrs.designer;

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
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class ActionSearch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSearch.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if(!effectivePerson.isManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		logger.info("{}开始数据中心设计搜索，关键字：{}", effectivePerson.getDistinguishedName(), wi.getKeyword());
		if(StringUtils.isBlank(wi.getKeyword())){
			throw new ExceptionFieldEmpty("keyword");
		}
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> resWos = new ArrayList<>();
		List<CompletableFuture<List<Wo>>> list = new ArrayList<>();
		if (wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.view.toString())){
			list.add(searchView(wi, wi.getAppIdList()));
		}
		if (wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.table.toString())){
			list.add(searchTable(wi, wi.getAppIdList()));
		}
		if (wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.statement.toString())){
			list.add(searchStatement(wi, wi.getAppIdList()));
		}
		if (wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.stat.toString())){
			list.add(searchStat(wi, wi.getAppIdList()));
		}
		for (CompletableFuture<List<Wo>> cf : list){
			if(resWos.size()<50) {
				resWos.addAll(cf.get(60, TimeUnit.SECONDS));
			}
		}
		if (resWos.size()>50){
			resWos = resWos.subList(0, 50);
		}
		result.setData(resWos);
		result.setCount((long)resWos.size());
		return result;
	}

	private CompletableFuture<List<Wo>> searchView(final Wi wi, final List<String> appIdList) {
		CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoView> woViews;
				if (ListTools.isEmpty(appIdList)) {
					woViews = emc.fetchAll(View.class, WoView.copier);
				} else {
					woViews = emc.fetchIn(View.class, WoView.copier, View.query_FIELDNAME, appIdList);
				}
				for (WoView woView : woViews) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoView.copier.getCopyFields(), woView, wi.getKeyword(),
							wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.find(woView.getQuery(), Query.class);
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
			}catch (Exception e){
				logger.error(e);
			}
			return resWos;
		});
		return cf;
	}

	private CompletableFuture<List<Wo>> searchTable(final Wi wi, final List<String> appIdList) {
		CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoTable> woTables;
				if (ListTools.isEmpty(appIdList)) {
					woTables = emc.fetchAll(Table.class, WoTable.copier);
				} else {
					woTables = emc.fetchIn(Table.class, WoTable.copier, Table.query_FIELDNAME, appIdList);
				}
				for (WoTable woTable : woTables) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoTable.copier.getCopyFields(), woTable, wi.getKeyword(),
							wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.find(woTable.getQuery(), Query.class);
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
			}catch (Exception e){
				logger.error(e);
			}
			return resWos;
		});
		return cf;
	}

	private CompletableFuture<List<Wo>> searchStat(final Wi wi, final List<String> appIdList) {
		CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoStat> woStats;
				if (ListTools.isEmpty(appIdList)) {
					woStats = emc.fetchAll(Stat.class, WoStat.copier);
				} else {
					woStats = emc.fetchIn(Stat.class, WoStat.copier, Stat.query_FIELDNAME, appIdList);
				}
				for (WoStat woStat : woStats) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoStat.copier.getCopyFields(), woStat, wi.getKeyword(),
							wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.find(woStat.getQuery(), Query.class);
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
			}catch (Exception e){
				logger.error(e);
			}
			return resWos;
		});
		return cf;
	}

	private CompletableFuture<List<Wo>> searchStatement(final Wi wi, final List<String> appIdList) {
		CompletableFuture<List<Wo>> cf = CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoStatement> woStatements;
				if (ListTools.isEmpty(appIdList)) {
					woStatements = emc.fetchAll(Statement.class, WoStatement.copier);
				} else {
					woStatements = emc.fetchIn(Statement.class, WoStatement.copier, Statement.query_FIELDNAME, appIdList);
				}
				for (WoStatement woStatement : woStatements) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoStatement.copier.getCopyFields(), woStatement, wi.getKeyword(),
							wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Query query = emc.find(woStatement.getQuery(), Query.class);
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
			}catch (Exception e){
				logger.error(e);
			}
			return resWos;
		});
		return cf;
	}

	public static class Wi extends WiDesigner {

	}

	public static class Wo extends WrapDesigner{

	}

	public static class WoView extends View {

		static WrapCopier<View, WoView> copier = WrapCopierFactory.wo(View.class, WoView.class,
				JpaObject.singularAttributeField(View.class, true, false),null);

	}

	public static class WoStat extends Stat {

		static WrapCopier<Stat, WoStat> copier = WrapCopierFactory.wo(Stat.class, WoStat.class,
				JpaObject.singularAttributeField(Stat.class, true, false),null);

	}

	public static class WoTable extends Table {

		static WrapCopier<Table, WoTable> copier = WrapCopierFactory.wo(Table.class, WoTable.class,
				JpaObject.singularAttributeField(Table.class, true, false),null);

	}

	public static class WoStatement extends Statement {

		static WrapCopier<Statement, WoStatement> copier = WrapCopierFactory.wo(Statement.class, WoStatement.class,
				JpaObject.singularAttributeField(Statement.class, true, false),null);

	}


}

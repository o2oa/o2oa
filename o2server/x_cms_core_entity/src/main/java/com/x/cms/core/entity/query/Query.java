package com.x.cms.core.entity.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class Query extends GsonPropertyObject {

	public Query() {
		this.scopeType = ScopeType.published;
		this.selectEntryList = new ArrayList<SelectEntry>();
		this.whereEntry = new WhereEntry();
		this.restrictWhereEntry = new WhereEntry();
		this.filterEntryList = new ArrayList<FilterEntry>();
		this.customFilterEntryList = new ArrayList<CustomFilterEntry>();
		this.restrictFilterEntryList = new ArrayList<FilterEntry>();
		this.dateRangeEntry = new DateRangeEntry();
		this.restrictDateRangeEntry = new DateRangeEntry();
		this.orderEntryList = new ArrayList<OrderEntry>();
		this.groupEntry = new GroupEntry();
		this.calculate = new Calculate();
		this.columnList = new ArrayList<String>();
	}

	private ScopeType scopeType;

	private List<SelectEntry> selectEntryList;

	private WhereEntry whereEntry;

	private WhereEntry restrictWhereEntry;

	private List<FilterEntry> filterEntryList;

	private List<CustomFilterEntry> customFilterEntryList;

	private List<FilterEntry> restrictFilterEntryList;

	private DateRangeEntry dateRangeEntry;

	private DateRangeEntry restrictDateRangeEntry;

	private List<OrderEntry> orderEntryList;

	private GroupEntry groupEntry;

	private List<String> columnList;

	private Calculate calculate;

	private String afterGridScriptText;

	private String afterGroupGridScriptText;

	private String afterCalculateGridScriptText;

	private Table grid;

	private List<LinkedHashMap<String, Object>> groupGrid;

	private List<?> calculateGrid;

	private List<CalculateCell> calculateAmountGrid;

	private List<Object> columnGrid;

	private Boolean restrictAccessible = false;

	public List<SelectEntry> getSelectEntryList() {
		return selectEntryList;
	}

	public void setSelectEntryList(List<SelectEntry> selectEntryList) {
		this.selectEntryList = selectEntryList;
	}

	public WhereEntry getWhereEntry() {
		return whereEntry;
	}

	public void setWhereEntry(WhereEntry whereEntry) {
		this.whereEntry = whereEntry;
	}

	public List<FilterEntry> getFilterEntryList() {
		return filterEntryList;
	}

	public void setFilterEntryList(List<FilterEntry> filterEntryList) {
		this.filterEntryList = filterEntryList;
	}

	public GroupEntry getGroupEntry() {
		return groupEntry;
	}

	public void setGroupEntry(GroupEntry groupEntry) {
		this.groupEntry = groupEntry;
	}

	public DateRangeEntry getDateRangeEntry() {
		return dateRangeEntry;
	}

	public void setDateRangeEntry(DateRangeEntry dateRangeEntry) {
		this.dateRangeEntry = dateRangeEntry;
	}

	public List<OrderEntry> getOrderEntryList() {
		return orderEntryList;
	}

	public void setOrderEntryList(List<OrderEntry> orderEntryList) {
		this.orderEntryList = orderEntryList;
	}

	public String getAfterGridScriptText() {
		return afterGridScriptText;
	}

	public void setAfterGridScriptText(String afterGridScriptText) {
		this.afterGridScriptText = afterGridScriptText;
	}

	public String getAfterGroupGridScriptText() {
		return afterGroupGridScriptText;
	}

	public void setAfterGroupGridScriptText(String afterGroupGridScriptText) {
		this.afterGroupGridScriptText = afterGroupGridScriptText;
	}

	public String getAfterCalculateGridScriptText() {
		return afterCalculateGridScriptText;
	}

	public void setAfterCalculateGridScriptText(String afterCalculateGridScriptText) {
		this.afterCalculateGridScriptText = afterCalculateGridScriptText;
	}

	public WhereEntry getRestrictWhereEntry() {
		return restrictWhereEntry;
	}

	public void setRestrictWhereEntry(WhereEntry restrictWhereEntry) {
		this.restrictWhereEntry = restrictWhereEntry;
	}

	public List<FilterEntry> getRestrictFilterEntryList() {
		return restrictFilterEntryList;
	}

	public void setRestrictFilterEntryList(List<FilterEntry> restrictFilterEntryList) {
		this.restrictFilterEntryList = restrictFilterEntryList;
	}

	public List<Row> getGrid() {
		return grid;
	}

	public void setGrid(Table grid) {
		this.grid = grid;
	}

	private void adjust() throws Exception {
		this.adjustScopeType();
		this.adjustDateRangeEntry();
		this.adjustRestrictDateRangeEntry();
		/* 先调整slectEntry 顺序不能改 */
		this.adjustSelectEntryList();
		this.adjustOrderEntryList();
		this.adjustGroupEntry();
		this.adjustCalculate();
	}

	private void adjustScopeType() throws Exception {
		if (this.scopeType == null) {
			this.scopeType = ScopeType.published;
		}
	}

	private void adjustDateRangeEntry() throws Exception {
		if (null == this.getDateRangeEntry()) {
			this.setDateRangeEntry(new DateRangeEntry());
			this.getDateRangeEntry().setDateRangeType(DateRangeType.none);
		}
		this.transformDateRangeEntry(this.getDateRangeEntry());
	}

	private void adjustRestrictDateRangeEntry() throws Exception {
		if (null == this.getRestrictDateRangeEntry()) {
			this.setRestrictDateRangeEntry(new DateRangeEntry());
			this.getRestrictDateRangeEntry().setDateRangeType(DateRangeType.none);
		}
		this.transformDateRangeEntry(this.getRestrictDateRangeEntry());
	}

	private void adjustSelectEntryList() throws Exception {
		List<SelectEntry> list = new ArrayList<>();
		for (SelectEntry o : this.getSelectEntryList()) {
			if (o.available()) {
				list.add(o);
			}
		}
		this.setSelectEntryList(list);
	}

	private void adjustGroupEntry() throws Exception {
		GroupEntry o = null;
		if (null != this.getGroupEntry() && this.getGroupEntry().available()) {
			for (SelectEntry selectEntry : this.getSelectEntryList()) {
				if (StringUtils.equals(this.getGroupEntry().getColumn(), selectEntry.getColumn())) {
					o = this.getGroupEntry();
					break;
				}
			}
		}
		this.setGroupEntry(o);
	}

	private void adjustOrderEntryList() throws Exception {
		List<OrderEntry> list = new ArrayList<>();
		for (OrderEntry o : this.getOrderEntryList()) {
			if (o.available()) {
				inner: for (SelectEntry selectEntry : this.getSelectEntryList()) {
					if (StringUtils.equals(o.getColumn(), selectEntry.getColumn())) {
						list.add(o);
						break inner;
					}
				}
			}
		}
		this.setOrderEntryList(list);
	}

	private void adjustCalculate() throws Exception {

	}

	private void transformDateRangeEntry(DateRangeEntry entry) throws Exception {
		Date now = new Date();
		if (null == entry.getAdjust()) {
			entry.setAdjust(0);
		}
		if (null == entry.getDateEffectType()) {
			entry.setDateEffectType(DateEffectType.publish);
		}
		if (null == entry.getDateRangeType()) {
			entry.setDateRangeType(DateRangeType.none);
		}
		String year = entry.getYear();
		String month = entry.getMonth();
		String date = entry.getDate();
		Integer week = entry.getWeek();
		Integer season = entry.getSeason();
		Integer adjust = entry.getAdjust();
		switch (entry.getDateRangeType()) {
		case year:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			entry.setStart(DateTools.floorYear(year, adjust));
			entry.setCompleted(DateTools.ceilYear(year, adjust));
			break;
		case season:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (null == season) {
				season = DateTools.season(now);
			}
			entry.setStart(DateTools.floorSeason(year, season, adjust));
			entry.setCompleted(DateTools.ceilSeason(year, season, adjust));
			break;
		case month:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (StringUtils.isEmpty(month)) {
				month = DateTools.format(now, DateTools.format_MM);
			}
			entry.setStart(DateTools.floorMonth(year, month, adjust));
			entry.setCompleted(DateTools.ceilMonth(year, month, adjust));
			break;
		case week:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (null == week) {
				week = DateTools.week(now);
			}
			entry.setStart(DateTools.floorWeekOfYear(year, week, adjust));
			entry.setCompleted(DateTools.ceilWeekOfYear(year, week, adjust));
			break;
		case date:
			if (StringUtils.isEmpty(year)) {
				year = DateTools.format(now, DateTools.format_yyyy);
			}
			if (StringUtils.isEmpty(month)) {
				month = DateTools.format(now, DateTools.format_MM);
			}
			if (StringUtils.isEmpty(date)) {
				date = DateTools.format(now, DateTools.format_dd);
			}
			entry.setStart(DateTools.floorDate(year, month, date, adjust));
			entry.setCompleted(DateTools.ceilDate(year, month, date, adjust));
			break;
		case range:
			if (null == entry.getStart() || null == entry.getCompleted()) {
				throw new Exception("begin or end can not be null when dateRangeEntry on type appoint.");
			}
			break;
		default:
			break;
		}
	}

	public void query() throws Exception {
		/* 先获取所有记录对应的job值作为返回的结果集 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/* 先进行字段调整 */
			this.adjust();
			List<String> docIds = this.listDocIds(emc);
			Table table = this.concreteTable(docIds);
			if (this.selectEntryListAvailable()) {
				this.fillSelectEntries(emc, docIds, table);
			}
			table = OrderEntryTools.order(table, this.getOrderEntryList());
			ScriptEngine scriptEngine = (StringUtils.isNotEmpty(this.getAfterGridScriptText())
					|| StringUtils.isNotEmpty(this.getAfterGroupGridScriptText())
					|| StringUtils.isNotEmpty(this.getAfterCalculateGridScriptText())) == true
							? this.createScriptEngine()
							: null;
			if (StringUtils.isNotEmpty(this.getAfterGridScriptText())) {
				scriptEngine.put("grid", table);
				scriptEngine.eval(this.getAfterGridScriptText());
			}
			this.setGrid(table);
			if ((null != this.getGroupEntry()) && this.getGroupEntry().available()) {
				List<LinkedHashMap<String, Object>> groupGrid = GroupEntryTools.group(table, this.getGroupEntry(),
						this.getOrderEntryList());
				if (StringUtils.isNotEmpty(this.getAfterGroupGridScriptText())) {
					scriptEngine.put("groupGrid", groupGrid);
					scriptEngine.eval(this.getAfterGroupGridScriptText());
				}
				this.setGroupGrid(groupGrid);
				/* 此部分的功能在前台整理数据时完成 */
				/* 如果分组输出了那么就不输出grid减少前台js解析Json的开销. */
				// this.setGrid(null);
			}
			if ((null != this.calculate) && (this.calculate.available())) {
				calculateGrid = CalculateEntryTools.calculate(table, this.calculate, this.getGroupEntry());
				if (this.calculate.getIsAmount()) {
					calculateAmountGrid = CalculateEntryTools.calculateAmount(table,
							this.calculate.getCalculateEntryList());
				}
				if (StringUtils.isNotEmpty(this.getAfterCalculateGridScriptText())) {
					scriptEngine.put("calculateGrid", this.getCalculateGrid());
					scriptEngine.eval(this.getAfterCalculateGridScriptText());
				}
			}
			/* 需要抽取单独的列 */
			if (ListTools.isNotEmpty(this.columnList)) {
				this.columnGrid = new ArrayList<Object>();
				for (String column : this.columnList) {
					List<Object> list = new ArrayList<>();
					for (Row o : table) {
						list.add(o.get(column));
					}
					/* 只有一列的情况下直接输出List */
					if (this.columnList.size() == 1) {
						this.columnGrid = list;
					} else {
						this.columnGrid.add(list);
					}
				}
			}
		}
	}

	private Boolean selectEntryListAvailable() {
		for (SelectEntry o : ListTools.nullToEmpty(this.selectEntryList)) {
			if (o.available()) {
				return true;
			}
		}
		return false;
	}

	private List<String> listDocIds(EntityManagerContainer emc) throws Exception {
		List<String> jobs = new ArrayList<>();

		jobs.addAll(this.listDocIdsWithCondition(emc));

		/** 针对DataItem进行判断 */
		if (ListTools.isNotEmpty(this.getRestrictFilterEntryList())
				|| ListTools.isNotEmpty(this.getFilterEntryList())) {
			EntityManager em = emc.get(Item.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			/** 运行RestrictFilterEntry */
			if (ListTools.isNotEmpty(this.getRestrictFilterEntryList())) {
				List<String> partDocs = new ArrayList<>();
				for (int i = 0; i < this.getRestrictFilterEntryList().size(); i++) {
					FilterEntry f = this.getRestrictFilterEntryList().get(i);
					if (f.available()) {
						CriteriaQuery<String> cq = cb.createQuery(String.class);
						Root<Item> root = cq.from(Item.class);
						Predicate p = FilterEntryTools.toPredicate(cb, root, f);
						p = cb.and(p, root.get(Item_.bundle).in(jobs));
						cq.select(root.get(Item_.bundle)).where(p);
						List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
						/** 不等于在这里单独通过等于处理 */
						if (Comparison.isNotEquals(f.getComparison())) {
							os = ListUtils.subtract(jobs, os);
						}
						if (i == 0) {
							partDocs.addAll(os);
						} else {
							if (StringUtils.equals("and", f.getLogic())) {
								partDocs = ListUtils.intersection(partDocs, os);
							} else {
								partDocs = ListUtils.union(partDocs, os);
							}
						}
					}
				}
				jobs = ListUtils.intersection(jobs, partDocs);
			}
			/** 运行FilterEntry */
			if (ListTools.isNotEmpty(this.getFilterEntryList())) {
				List<String> partDocs = new ArrayList<>();
				for (int i = 0; i < this.getFilterEntryList().size(); i++) {
					FilterEntry f = this.getFilterEntryList().get(i);
					if (f.available()) {
						CriteriaQuery<String> cq = cb.createQuery(String.class);
						Root<Item> root = cq.from(Item.class);
						Predicate p = FilterEntryTools.toPredicate(cb, root, f);
						p = cb.and(p, root.get(Item_.bundle).in(jobs));
						cq.select(root.get(Item_.bundle)).where(p);
						List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
						/** 不等于在这里单独通过等于处理 */
						if (Comparison.isNotEquals(f.getComparison())) {
							os = ListUtils.subtract(jobs, os);
						}
						if (i == 0) {
							partDocs.addAll(os);
						} else {
							if (StringUtils.equals("and", f.getLogic())) {
								partDocs = ListUtils.intersection(partDocs, os);
							} else {
								partDocs = ListUtils.union(partDocs, os);
							}
						}
					}
				}
				jobs = ListUtils.intersection(jobs, partDocs);
			}
		}
		return jobs;
	}

	private List<String> listDocIdsWithCondition(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Document.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Document> root = cq.from(Document.class);
		Predicate pd = cb.and(DateRangeEntryTools.toDocumentPredicate(cb, root, this.getRestrictDateRangeEntry()),
				DateRangeEntryTools.toDocumentPredicate(cb, root, this.getDateRangeEntry()));
		Predicate pw = cb.and(WhereEntryTools.toDocumentPredicate(cb, root, this.getRestrictWhereEntry()),
				WhereEntryTools.toDocumentPredicate(cb, root, this.getWhereEntry()));
		cq.select(root.get(Document.id_FIELDNAME)).where(cb.and(pd, pw));
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private Table concreteTable(List<String> docIds) throws Exception {
		Table table = new Table();
		for (String str : docIds) {
			Row row = new Row(str);
			for (SelectEntry entry : ListTools.nullToEmpty(this.selectEntryList)) {
				if (entry.available()) {
					row.put(entry.getColumn(), Objects.toString(entry.getDefaultValue(), ""));
				}
			}
			table.add(row);
		}
		return table;
	}

	private void fillSelectEntries(EntityManagerContainer emc, List<String> docIds, Table table) throws Exception {
		List<SelectEntry> attributeSelectEntries = SelectEntryTools
				.filterAttributeSelectEntries(this.getSelectEntryList());
		List<SelectEntry> pathSelectEntries = SelectEntryTools.filterPathSelectEntries(this.getSelectEntryList());
		this.fillAttributeSelectEntries(emc, docIds, attributeSelectEntries, table);
		this.fillPathSelectEntries(emc, docIds, pathSelectEntries, table);
	}

	private void fillAttributeSelectEntries(EntityManagerContainer emc, List<String> docIds,
			List<SelectEntry> selectEntries, Table table) throws Exception {
		this.fillAttributeSelectEntriesDocument(emc, docIds, selectEntries, table);
	}

	private void fillAttributeSelectEntriesDocument(EntityManagerContainer emc, List<String> docIds,
			List<SelectEntry> selectEntries, Table table) throws Exception {
		EntityManager em = emc.get(Document.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Document> root = cq.from(Document.class);
		List<Selection<?>> selections = new ArrayList<>();
		Selection<Object> selectionDoc = null;
		for (SelectEntry en : selectEntries) {
			if (en.available()) {
				if (StringUtils.equals(Document.id_FIELDNAME, en.getAttribute())) {
					selectionDoc = root.get(Document.id_FIELDNAME).alias(en.getColumn());
					selections.add(selectionDoc);
				} else {
					selections.add(root.get(en.getAttribute()).alias(en.getColumn()));
				}
			}
		}
		if (selectionDoc == null) {
			selectionDoc = root.get(Document.id_FIELDNAME);
			cq.multiselect(ListTools.add(selections, true, false, selectionDoc));
		} else {
			cq.multiselect(selections);
		}
		cq.where(root.get(Document.id_FIELDNAME).in(docIds));
		List<Tuple> tuples = em.createQuery(cq).getResultList();
		for (Tuple tuple : tuples) {
			Object job = tuple.get(selectionDoc);
			Row row = table.get(job.toString());
			for (Selection<?> selection : selections) {
				/* 前面已经填充了默认值,如果是null那么跳过这个值 */
				if (null != tuple.get(selection)) {
					row.put(selection.getAlias(), tuple.get(selection));
				}
			}
		}
	}

	private void fillPathSelectEntries(EntityManagerContainer emc, List<String> docIds, List<SelectEntry> selectEntries,
			Table table) throws Exception {
		EntityManager em = emc.get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		for (SelectEntry selectEntry : selectEntries) {
			CriteriaQuery<Item> cq = cb.createQuery(Item.class);
			Root<Item> root = cq.from(Item.class);
			Predicate p = root.get(Item.bundle_FIELDNAME).in(docIds);
			String[] paths = StringUtils.split(selectEntry.getPath(), ".");
			p = cb.and(p, cb.equal(root.get(Item_.path0), paths.length > 0 ? paths[0] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path1), paths.length > 1 ? paths[1] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path2), paths.length > 2 ? paths[2] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path3), paths.length > 3 ? paths[3] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path4), paths.length > 4 ? paths[4] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path5), paths.length > 5 ? paths[5] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path6), paths.length > 6 ? paths[6] : ""));
			p = cb.and(p, cb.equal(root.get(Item_.path7), paths.length > 7 ? paths[7] : ""));
			cq.select(root).where(p);
			List<Item> list = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
			for (Item o : list) {
				Row row = table.get(o.getBundle());
				switch (o.getItemPrimitiveType()) {
				case s:
					switch (o.getItemStringValueType()) {
					case s:
						if (null != o.getStringValue()) {
							row.put(selectEntry.getColumn(), o.getStringValue());
						}
						break;
					case d:
						if (null != o.getDateValue()) {
							row.put(selectEntry.getColumn(), JpaObjectTools.confirm(o.getDateValue()));
						}
						break;
					case t:
						if (null != o.getTimeValue()) {
							row.put(selectEntry.getColumn(), JpaObjectTools.confirm(o.getTimeValue()));
						}
						break;
					case dt:
						if (null != o.getDateTimeValue()) {
							row.put(selectEntry.getColumn(), JpaObjectTools.confirm(o.getDateTimeValue()));
						}
						break;
					default:
						// row.put(selectEntry.getColumn(), null);
						break;
					}
					break;
				case n:
					if (null != o.getNumberValue()) {
						row.put(selectEntry.getColumn(), o.getNumberValue());
					}
					break;
				case b:
					if (null != o.getBooleanValue()) {
						row.put(selectEntry.getColumn(), o.getBooleanValue());
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private ScriptEngine createScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		return engine;
	}

	public DateRangeEntry getRestrictDateRangeEntry() {
		return restrictDateRangeEntry;
	}

	public void setRestrictDateRangeEntry(DateRangeEntry restrictDateRangeEntry) {
		this.restrictDateRangeEntry = restrictDateRangeEntry;
	}

	public ScopeType getScopeType() {
		return scopeType;
	}

	public void setScopeType(ScopeType scopeType) {
		this.scopeType = scopeType;
	}

	public List<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}

	public Calculate getCalculate() {
		return calculate;
	}

	public void setCalculate(Calculate calculate) {
		this.calculate = calculate;
	}

	public List<LinkedHashMap<String, Object>> getGroupGrid() {
		return groupGrid;
	}

	public void setGroupGrid(List<LinkedHashMap<String, Object>> groupGrid) {
		this.groupGrid = groupGrid;
	}

	public List<?> getCalculateGrid() {
		return calculateGrid;
	}

	public void setCalculateGrid(List<?> calculateGrid) {
		this.calculateGrid = calculateGrid;
	}

	public List<Object> getColumnGrid() {
		return columnGrid;
	}

	public void setColumnGrid(List<Object> columnGrid) {
		this.columnGrid = columnGrid;
	}

	public List<CalculateCell> getCalculateAmountGrid() {
		return calculateAmountGrid;
	}

	public void setCalculateAmountGrid(List<CalculateCell> calculateAmountGrid) {
		this.calculateAmountGrid = calculateAmountGrid;
	}

	public List<CustomFilterEntry> getCustomFilterEntryList() {
		return customFilterEntryList;
	}

	public void setCustomFilterEntryList(List<CustomFilterEntry> customFilterEntryList) {
		this.customFilterEntryList = customFilterEntryList;
	}

	public Boolean getRestrictAccessible() {
		return restrictAccessible;
	}

	public void setRestrictAccessible(Boolean restrictAccessible) {
		this.restrictAccessible = restrictAccessible;
	}

}
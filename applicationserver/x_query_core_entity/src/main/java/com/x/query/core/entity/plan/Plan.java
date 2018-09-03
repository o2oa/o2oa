package com.x.query.core.entity.plan;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.ItemPrimitiveType;
import com.x.base.core.entity.dataitem.ItemStringValueType;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public abstract class Plan extends GsonPropertyObject {

	private static Logger logger = LoggerFactory.getLogger(Plan.class);

	public static final String SCOPE_WORK = "work";
	public static final String SCOPE_CMS_INFO = "cms_info";
	public static final String SCOPE_CMS_DATA = "cms_data";
	public static final String SCOPE_WORKCOMPLETED = "workCompleted";
	public static final String SCOPE_ALL = "all";

	public static final String CALCULATE_SUM = "sum";
	public static final String CALCULATE_AVERAGE = "average";
	public static final String CALCULATE_COUNT = "count";

	protected static Pattern DISTINGUISHEDNAME_PATTERN = Pattern.compile("^(\\S+)\\@(\\S+)\\@(P|PA|G|R|I|U|UA|UD)$");

	protected static final int SQL_STATEMENT_IN_BATCH = 3000;

	public Runtime runtime;

	public SelectEntries selectList = new SelectEntries();

	public List<FilterEntry> filterList = new TreeList<>();

	public List<FilterEntry> customFilterList = new TreeList<>();

	/* 需要输出,前台也需要使用此值 */
	public List<SelectEntry> orderList = new TreeList<>();

	/* 需要输出,前台也需要使用此值 */
	public SelectEntry group = null;

	public List<String> columnList = new TreeList<>();

	public Calculate calculate = new Calculate();

	public String afterGridScriptText = "";

	public String afterGroupGridScriptText = "";

	public String afterCalculateGridScriptText = "";

	public Table grid;

	public GroupTable groupGrid;

	public List<?> calculateGrid;

	public List<Object> columnGrid;

	public Boolean exportGrid;

	public Boolean exportGroupGrid;

	public Integer count;

	private Table order(Table table) {
		Comparator<Row> comparator = new Comparator<Row>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(Row r1, Row r2) {
				int comp = 0;
				for (SelectEntry en : orderList) {
					Object o1 = r1.get(en.column);
					Object o2 = r2.get(en.column);
					if (null == o1 && null == o2) {
						comp = 0;
					} else if (null == o1) {
						comp = -1;
					} else if (null == o2) {
						comp = 1;
					} else {
						Comparable c1 = (Comparable) o1;
						Comparable c2 = (Comparable) o2;
						if (StringUtils.equals(SelectEntry.ORDER_ASC, en.orderType)) {
							comp = c1.compareTo(c2);
						} else {
							comp = c2.compareTo(c1);
						}
					}
					if (comp != 0) {
						return comp;
					}
				}
				return comp;
			}
		};
		List<Row> list = new TreeList<>();
		if ((null != table) && (!table.isEmpty()) && (!orderList.isEmpty())) {
			list = table.stream().sorted(comparator).collect(Collectors.toList());
			table.clear();
			table.addAll(list);
		}
		return table;
	}

	/*
	 * [ { "column": "C7AC7F427FC0000141704670375F79F0", "displayName": "金额",
	 * "value": 2110.0 } ]
	 */
	private CalculateRow calculate(Table table, Calculate calculate) throws Exception {
		/** 非分类总计 */
		CalculateRow calculateRow = new CalculateRow();
		for (CalculateEntry o : calculate.calculateList) {
			switch (StringUtils.trimToEmpty(o.calculateType)) {
			case CALCULATE_SUM:
				calculateRow.add(new CalculateCell(o, o.sum(table)));
				break;
			case CALCULATE_AVERAGE:
				calculateRow.add(new CalculateCell(o, o.average(table)));
				break;
			default:
				calculateRow.add(new CalculateCell(o, o.count(table)));
				break;
			}
		}
		return calculateRow;
	}

	/*
	 * 分类计算输出格式 [ { "group": "报销申请", "list": [ { "column":
	 * "C7AC7F427FC0000141704670375F79F0", "displayName": "金额", "value": 1000 } ] },
	 * { "group": "项目经理审批", "list": [ { "column":
	 * "C7AC7F427FC0000141704670375F79F0", "displayName": "金额", "value": 1110 } ] }
	 * ]
	 */
	private CalculateGroupTable calculateGroup() throws Exception {
		CalculateGroupTable calculateGroupTable = new CalculateGroupTable();
		for (GroupRow groupRow : this.groupGrid) {
			List<CalculateCell> list = new TreeList<>();
			for (CalculateEntry entry : calculate.calculateList) {
				switch (entry.calculateType) {
				case CALCULATE_SUM:
					list.add(new CalculateCell(entry, entry.sum(groupRow.list)));
					break;
				case CALCULATE_AVERAGE:
					list.add(new CalculateCell(entry, entry.average(groupRow.list)));
					break;
				default:
					list.add(new CalculateCell(entry, entry.count(groupRow.list)));
					break;
				}
			}
			CalculateGroupRow calculateGroupRow = new CalculateGroupRow();
			calculateGroupRow.group = groupRow.group;
			calculateGroupRow.list = list;
			calculateGroupTable.add(calculateGroupRow);
		}
		return calculateGroupTable;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private GroupTable group(Table table) throws Exception {
		final String orderType = (null == this.group) ? SelectEntry.ORDER_ORIGINAL : this.group.orderType;
		Map<Object, List<Row>> map = table.stream().collect(Collectors.groupingBy(row -> row.get(this.group.column)));
		List<GroupRow> groupRows = new GroupTable();
		for (Entry<Object, List<Row>> en : map.entrySet()) {
			Table o = new Table();
			o.addAll(en.getValue());
			o = order(o);
			GroupRow groupRow = new GroupRow();
			if (this.group.isName) {
				groupRow.group = this.name(Objects.toString(en.getKey()));
			} else {
				groupRow.group = en.getKey();
			}
			groupRow.list = o;
			groupRows.add(groupRow);
		}
		/* 分类值再进行一次排序 */
		groupRows = groupRows.stream().sorted((r1, r2) -> {
			Object o1 = r1.group;
			Object o2 = r2.group;
			if (null == o1 && null == o2) {
				return 0;
			} else if (null == o1) {
				return -1;
			} else if (null == o2) {
				return 1;
			} else {
				Comparable c1 = (Comparable) o1;
				Comparable c2 = (Comparable) o2;
				if (StringUtils.equals(SelectEntry.ORDER_ASC, orderType)) {
					return c1.compareTo(c2);
				} else {
					return c2.compareTo(c1);
				}
			}
		}).collect(Collectors.toList());
		GroupTable groupTable = new GroupTable();
		groupTable.addAll(groupRows);
		return groupTable;
	}

	abstract void adjust() throws Exception;

	abstract List<String> listBundle(EntityManagerContainer emc) throws Exception;

	public void access() throws Exception {
		/* 先获取所有记录对应的job值作为返回的结果集 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/* 先进行字段调整 */
			logger.debug("开始方案执行.");
			this.adjust();
			this.group = this.findGroupSelectEntry();
			this.orderList = this.listOrderSelectEntry();
			logger.debug("开始查找bundles.");
			List<String> bundles = this.listBundle(emc);
			logger.debug("完成bundles查找,共找到 {} 个bundle.", bundles.size());
			if ((null != this.count) && (this.count > 0)) {
				/* 默认限制了数量 */
				if (this.count < bundles.size()) {
					bundles = bundles.subList(0, this.count);
					logger.debug("完成默认数量限制,限制后共有 {} 个bundle.", bundles.size());
				}
			}
			if ((null != this.runtime.count) && (this.runtime.count > 0)) {
				/* runtime限制了数量 */
				if (this.runtime.count < bundles.size()) {
					bundles = bundles.subList(0, this.runtime.count);
					logger.debug("完成运行时数量限制,限制后共有 {} 个bundle.", bundles.size());
				}
			}
			logger.debug("开始构建输出表.");
			final Table fillTable = this.concreteTable(bundles);
			// ************************************
			// logger.debug("构建输出表完成.");
			// for (List<String> _part_bundles : ListTools.batch(bundles,
			// SQL_STATEMENT_IN_BATCH)) {
			// logger.debug("开始批次数据填充.");
			// this.fillSelectEntries(emc, _part_bundles, this.selectList, fillTable);
			// logger.debug("批次数据填充完成.");
			// }
			// ************************************
			List<CompletableFuture<Void>> futures = new TreeList<>();
			for (List<String> _part_bundles : ListTools.batch(bundles, SQL_STATEMENT_IN_BATCH)) {
				for (SelectEntry selectEntry : this.selectList) {
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
						logger.debug("开始批次数据填充.");
						try {
							this.fillSelectEntry(emc, _part_bundles, selectEntry, fillTable);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					futures.add(future);
				}
			}
			for (CompletableFuture<Void> future : futures) {
				future.get(300, TimeUnit.SECONDS);
				logger.debug("批次数据填充完成.");
			}
			// ************************************

			logger.debug("开始数据排序.");
			Table table = this.order(fillTable);
			logger.debug("数据排序完成.");
			ScriptEngine scriptEngine = (StringUtils.isNotEmpty(this.afterGridScriptText)
					|| StringUtils.isNotEmpty(this.afterGroupGridScriptText)
					|| StringUtils.isNotEmpty(this.afterCalculateGridScriptText)) == true ? this.createScriptEngine()
							: null;
			if (StringUtils.isNotEmpty(this.afterGridScriptText)) {
				scriptEngine.put("grid", table);
				scriptEngine.eval(this.afterGridScriptText);
			}
			this.grid = table;
			if (null != this.findGroupSelectEntry()) {
				logger.debug("开始数据分组.");
				GroupTable groupTable = group(table);
				logger.debug("数据分组完成.");
				if (StringUtils.isNotEmpty(this.afterGroupGridScriptText)) {
					scriptEngine.put("groupGrid", groupTable);
					scriptEngine.eval(this.afterGroupGridScriptText);
				}
				this.groupGrid = groupTable;
			}
			/** stat 统计部分 */
			if ((null != this.calculate) && (this.calculate.available())) {
				logger.debug("开始数据统计.");
				if (BooleanUtils.isTrue(this.calculate.isGroup)) {
					CalculateGroupTable calculateGroupTable = this.calculateGroup();
					this.calculateGrid = calculateGroupTable;
				} else {
					CalculateRow calculateRow = this.calculate(table, calculate);
					this.calculateGrid = calculateRow;
				}
				if (StringUtils.isNotEmpty(this.afterCalculateGridScriptText)) {
					scriptEngine.put("calculateGrid", this.calculateGrid);
					scriptEngine.eval(this.afterCalculateGridScriptText);
				}
				logger.debug("数据统计完成.");
			}
			/* 需要抽取单独的列 */
			if (ListTools.isNotEmpty(this.columnList)) {
				this.columnGrid = new TreeList<Object>();
				for (String column : this.columnList) {
					List<Object> list = new TreeList<>();
					SelectEntry selectEntry = this.selectList.column(column);
					if (null != selectEntry) {
						for (Row o : table) {
							if (selectEntry.isName) {
								list.add(name(Objects.toString(o.get(column), "")));
							} else {
								list.add(o.get(column));
							}
						}
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
		if (BooleanUtils.isFalse(exportGrid)) {
			this.grid = null;
		}
		if (BooleanUtils.isFalse(exportGroupGrid)) {
			this.groupGrid = null;
		}
		if (null != this.grid) {
			this.selectList.stream().filter(o -> {
				return BooleanUtils.isTrue(o.isName);
			}).forEach(o -> {
				this.grid.forEach(row -> {
					row.put(o.column, name(Objects.toString(row.get(o.column), "")));
				});
			});
		}
		if (null != this.groupGrid) {
			if (this.group.isName) {
				this.groupGrid.stream().forEach(o -> {
					o.group = name(Objects.toString(o.group, ""));
				});
			}
			this.selectList.stream().filter(o -> {
				return BooleanUtils.isTrue(o.isName);
			}).forEach(o -> {
				this.grid.forEach(row -> {
					row.put(o.column, name(Objects.toString(row.get(o.column), "")));
				});
			});
		}
	}

	private String name(String str) {
		Matcher m = DISTINGUISHEDNAME_PATTERN.matcher(str);
		if (m.find()) {
			return m.group(1);
		}
		return str;
	}

	private SelectEntry findGroupSelectEntry() {
		for (SelectEntry _o : this.selectList) {
			if (BooleanUtils.isTrue(_o.groupEntry)) {
				return _o;
			}
		}
		return null;
	}

	private List<SelectEntry> listOrderSelectEntry() {
		List<SelectEntry> list = new TreeList<>();
		for (SelectEntry _o : this.selectList) {
			if (StringUtils.equals(SelectEntry.ORDER_ASC, _o.orderType)
					|| StringUtils.equals(SelectEntry.ORDER_DESC, _o.orderType)) {
				list.add(_o);
			}
		}
		return list;
	}

	private Table concreteTable(List<String> jobs) throws Exception {
		Table table = new Table();
		for (String str : jobs) {
			Row row = new Row(str);
			for (SelectEntry entry : ListTools.trim(this.selectList, true, false)) {
				if (entry.available()) {
					/** 统一填充默认值 */
					row.put(entry.getColumn(), Objects.toString(entry.defaultValue, ""));
				}
			}
			table.add(row);
		}
		return table;
	}

	private void fillSelectEntry(EntityManagerContainer emc, List<String> jobs, SelectEntry selectEntry, Table table)
			throws Exception {
		EntityManager em = emc.get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		logger.debug("开始查找列:{}.", selectEntry);
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Item> root = cq.from(Item.class);
		// Predicate p = root.get(Item.bundle_FIELDNAME).in(jobs);
		HashMap<String, String> map = new HashMap<>();
		jobs.stream().forEach(o -> {
			map.put(o, o);
		});
		Expression<Set<String>> expression = cb.keys(map);
		// org.apache.openjpa.persistence.criteria.OpenJPACriteriaBuilder.
		Predicate p = cb.isMember(root.get(Item_.bundle), expression);
		String[] paths = StringUtils.split(selectEntry.path, ".");
		p = cb.and(p, cb.equal(root.get(Item_.path0), paths.length > 0 ? paths[0] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path1), paths.length > 1 ? paths[1] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path2), paths.length > 2 ? paths[2] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path3), paths.length > 3 ? paths[3] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path4), paths.length > 4 ? paths[4] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path5), paths.length > 5 ? paths[5] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path6), paths.length > 6 ? paths[6] : ""));
		p = cb.and(p, cb.equal(root.get(Item_.path7), paths.length > 7 ? paths[7] : ""));
		cq.multiselect(root.get(Item_.bundle), root.get(Item_.itemPrimitiveType), root.get(Item_.itemStringValueType),
				root.get(Item_.stringShortValue), root.get(Item_.dateValue), root.get(Item_.timeValue),
				root.get(Item_.dateTimeValue), root.get(Item_.booleanValue), root.get(Item_.numberValue)).where(p);
		List<Tuple> list = em.createQuery(cq).getResultList();
		logger.debug("完成查找列:{}.准备填充数据.", selectEntry);
		Row row = null;
		for (Tuple o : list) {
			row = table.get(Objects.toString(o.get(0)));
			switch (ItemPrimitiveType.valueOf(Objects.toString(o.get(1)))) {
			case s:
				switch (ItemStringValueType.valueOf(Objects.toString(o.get(2)))) {
				case s:
					if (null != o.get(3)) {
						row.put(selectEntry.getColumn(), Objects.toString(o.get(3)));
					}
					break;
				case d:
					if (null != o.get(4)) {
						row.put(selectEntry.getColumn(), JpaObjectTools.confirm((Date) o.get(4)));
					}
					break;
				case t:
					if (null != o.get(5)) {
						row.put(selectEntry.getColumn(), JpaObjectTools.confirm((Date) o.get(5)));
					}
					break;
				case dt:
					if (null != o.get(6)) {
						row.put(selectEntry.getColumn(), JpaObjectTools.confirm((Date) o.get(6)));
					}
					break;
				default:
					break;
				}
				break;
			case b:
				if (null != o.get(7)) {
					row.put(selectEntry.getColumn(), (Boolean) o.get(7));
				}
				break;
			case n:
				if (null != o.get(8)) {
					row.put(selectEntry.getColumn(), (Number) o.get(8));
				}
				break;
			default:
				break;
			}
		}
		logger.debug("完成填充数据.", selectEntry);
	}

	private ScriptEngine createScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		return engine;
	}
}

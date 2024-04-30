package com.x.query.core.express.plan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.ItemPrimitiveType;
import com.x.base.core.entity.dataitem.ItemStringValueType;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public abstract class Plan extends GsonPropertyObject {

	private static final Logger logger = LoggerFactory.getLogger(Plan.class);

	private static final long serialVersionUID = -4281507899642115426L;

	private static final Collator COLLATOR = Collator.getInstance(Locale.CHINESE);

	public static final String SCOPE_WORK = "work";
	public static final String SCOPE_CMS_INFO = "cms_info";
	public static final String SCOPE_CMS_DATA = "cms_data";
	public static final String SCOPE_WORKCOMPLETED = "workCompleted";
	public static final String SCOPE_ALL = "all";

	public static final String CALCULATE_SUM = "sum";
	public static final String CALCULATE_AVERAGE = "average";
	public static final String CALCULATE_COUNT = "count";

	protected transient ExecutorService threadPool;

	public void init(Runtime runtime, ExecutorService threadPool) {
		this.runtime = runtime;
		this.threadPool = threadPool;
	}

	public Runtime runtime;

	public SelectEntries selectList = new SelectEntries();

	public List<FilterEntry> filterList = new TreeList<>();

	public List<FilterEntry> customFilterList = new TreeList<>();

	public List<SelectEntry> orderList = new TreeList<>(); // 需要输出,前台也需要使用此值

	public SelectEntry group = null; // 需要输出,前台也需要使用此值

	public Table grid;

	public GroupTable groupGrid;

	public Integer count;

	private Table order(Table table) {
		if ((null != table) && (!table.isEmpty()) && (!orderList.isEmpty())) {
			TableRowComparator comparator = new TableRowComparator(this.orderList);
			List<Row> list = table.stream().sorted(comparator).collect(Collectors.toList());
			table.clear();
			table.addAll(list);
		}
		return table;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private GroupTable group(Table table) {
		final String orderType = (null == this.group) ? SelectEntry.ORDER_ORIGINAL : this.group.orderType;
		Map<Object, List<Row>> map = table.stream().collect(Collectors.groupingBy(row -> row.find(this.group.column)));
		List<GroupRow> groupRows = new GroupTable();
		for (Entry<Object, List<Row>> en : map.entrySet()) {
			Table o = new Table();
			o.addAll(en.getValue());
			// o = order(o);
			order(o);
			GroupRow groupRow = new GroupRow();
			if (BooleanUtils.isTrue(this.group.isName)) {
				groupRow.group = this.name(Objects.toString(en.getKey()));
			} else {
				groupRow.group = en.getKey();
			}
			groupRow.list = o;
			groupRows.add(groupRow);
		}
		// 分类值再进行一次排序
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
				if (o1 instanceof List) {
					String o = StringUtils.join((List) o1);
					if (StringUtils.isBlank(o)) {
						return -1;
					} else {
						o1 = o;
					}
				}
				if (o2 instanceof List) {
					String o = StringUtils.join((List) o2);
					if (StringUtils.isBlank(o)) {
						return 1;
					} else {
						o2 = o;
					}
				}
				Comparable c1 = (Comparable) o1;
				Comparable c2 = (Comparable) o2;
				if (StringUtils.equals(SelectEntry.ORDER_ASC, orderType)) {
					return COLLATOR.compare(c1, c2);
				} else {
					return COLLATOR.compare(c2, c1);
				}
			}
		}).collect(Collectors.toList());
		GroupTable groupTable = new GroupTable();
		groupTable.addAll(groupRows);
		return groupTable;
	}

	abstract void adjust() throws Exception;

	abstract List<String> listBundle() throws Exception;

	// 先获取所有记录对应的job值作为返回的结果集
	public void access() throws Exception {
		// 先进行字段调整
		this.adjust();
		this.group = this.findGroupSelectEntry();
		this.orderList = this.listOrderSelectEntry();
		List<String> bundles = null;
		if ((null != this.runtime) && (ListTools.isNotEmpty(runtime.bundleList))) {
			bundles = this.runtime.bundleList;
		} else {
			bundles = this.listBundle();
		}

		if ((null != this.runtime.count) && (this.runtime.count > 0) && (this.runtime.count < bundles.size())) {
			bundles = bundles.subList(0, this.runtime.count);
		}

		final Table fillTable = this.concreteTable(bundles);
		List<CompletableFuture<Void>> futures = new TreeList<>();
		for (List<String> _part_bundles : ListTools.batch(bundles, 500)) {
			for (SelectEntry selectEntry : this.selectList) {
				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
					try {
						this.fillSelectEntry(_part_bundles, selectEntry, fillTable);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, threadPool);
				futures.add(future);
			}
		}
		for (CompletableFuture<Void> future : futures) {
			future.get(300, TimeUnit.SECONDS);
		}
		Table table = this.order(fillTable);
		if (BooleanUtils.isFalse(this.selectList.emptyColumnCode())) {
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings();
			// ScriptContext scriptContext =
			// ScriptingFactory.scriptContextEvalInitialScript();
			// scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put("gird", table);
			bindings.putMember("gird", table);
			for (SelectEntry selectEntry : this.selectList) {
				if (StringTools.ifScriptHasEffectiveCode(selectEntry.code)) {
					List<ExtractObject> extractObjects = new TreeList<>();
					table.stream().forEach(r -> {
						ExtractObject extractObject = new ExtractObject();
						extractObject.setBundle(r.bundle);
						extractObject.setColumn(selectEntry.getColumn());
						extractObject.setValue(r.find(selectEntry.getColumn()));
						extractObject.setEntry(r);
						extractObjects.add(extractObject);
					});
					// scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put("extractObjects",
					// extractObjects);
					bindings.putMember("extractObjects", extractObjects);
					StringBuilder text = new StringBuilder();
					text.append("function executeScript(o){\n");
					text.append(selectEntry.code);
					text.append("\n");
					text.append("}\n");
					text.append("for (var key in extractObjects) {\n");
					text.append("var extractObject = extractObjects[key]\n");
					text.append("var obj = {\n");
					text.append("'value':extractObject.getValue(),\n");
					text.append("'entry':extractObject.getEntry(),\n");
					text.append("'columnName':extractObject.getColumn()\n");
					text.append("}\n");
					text.append("extractObject.setValue(executeScript.apply(obj)?.toString());\n");
					text.append("}");
					// CompiledScript cs = ScriptingFactory.compile(text.toString());
					Source source = GraalvmScriptingFactory.source(text.toString());
//					JsonScriptingExecutor.eval(cs, scriptContext);
					GraalvmScriptingFactory.eval(source, bindings);
					for (ExtractObject extractObject : extractObjects) {
						table.get(extractObject.getBundle()).put(extractObject.getColumn(), extractObject.getValue());
					}
				}
			}
		}

		this.grid = table;
		if (null != this.findGroupSelectEntry()) {
			this.groupGrid = group(table);
		}

		if (null != this.grid) {
			this.selectList.stream().filter(o -> BooleanUtils.isTrue(o.isName)).forEach(
					o -> this.grid.forEach(row -> row.put(o.column, name(Objects.toString(row.find(o.column), "")))));
		}
		if (null != this.groupGrid) {
			if (BooleanUtils.isTrue(this.group.isName)) {
				this.groupGrid.stream().forEach(o -> o.group = name(Objects.toString(o.group, "")));
			}
			this.selectList.stream().filter(o -> BooleanUtils.isTrue(o.isName)).forEach(
					o -> this.grid.forEach(row -> row.put(o.column, name(Objects.toString(row.find(o.column), "")))));
		}
	}

	public List<String> fetchBundles() throws Exception {
		// 先进行字段调整
		this.adjust();
		// 先获取所有记录对应的job值作为返回的结果集
		List<String> bundles = this.listBundle();
		this.group = this.findGroupSelectEntry();
		this.orderList = this.listOrderSelectEntry();
		if ((null != this.runtime.count) && (this.runtime.count > 0) && (this.runtime.count < bundles.size())) {
			bundles = bundles.subList(0, this.runtime.count);
		}
		if (orderList.isEmpty()) {
			return bundles;
		}
		TreeList<String> os = new TreeList<>();
		final Table fillTable = this.concreteTable(bundles);
		List<CompletableFuture<Void>> futures = new TreeList<>();
		for (List<String> _part_bundles : ListTools.batch(bundles, 500)) {
			for (SelectEntry selectEntry : this.orderList) {
				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
					try {
						this.fillSelectEntry(_part_bundles, selectEntry, fillTable);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, threadPool);
				futures.add(future);
			}
		}
		for (CompletableFuture<Void> future : futures) {
			future.get(300, TimeUnit.SECONDS);
		}
		Table table = this.order(fillTable);
		if (null == group) {
			for (Row row : table) {
				os.add(row.bundle);
			}
		} else {
			for (GroupRow groupRow : group(table)) {
				for (Row row : groupRow.list) {
					os.add(row.bundle);
				}
			}
		}
		return os;
	}

	private String name(String str) {
		Matcher m = OrganizationDefinition.distinguishedName_pattern.matcher(str);
		if (m.find()) {
			return m.group(1);
		}
		return str;
	}

	private SelectEntry findGroupSelectEntry() {
		for (SelectEntry o : this.selectList) {
			if (BooleanUtils.isTrue(o.groupEntry)) {
				return o;
			}
		}
		return null;
	}

	private List<SelectEntry> listOrderSelectEntry() {
		List<SelectEntry> list = new TreeList<>();
		SelectEntry g = this.findGroupSelectEntry();
		if ((null != g) && (g.isOrderType())) {
			list.add(g);
		}
		for (SelectEntry o : this.selectList) {
			if (o.isOrderType()) {
				list.add(o);
			}
		}
		return list;
	}

	private Table concreteTable(List<String> jobs) {
		Table table = new Table();
		for (String str : jobs) {
			Row row = new Row(str);
			for (SelectEntry entry : ListTools.trim(this.selectList, true, false)) {
				if (BooleanUtils.isTrue(entry.available())) {
					// 统一填充默认值
					row.put(entry.getColumn(), Objects.toString(entry.defaultValue, ""));
				}
			}
			table.add(row);
		}
		return table;
	}

	private void fillSelectEntry(List<String> bundles, SelectEntry selectEntry, Table table) throws Exception {
		if(StringUtils.isBlank(selectEntry.path)){
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Item.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Item> root = cq.from(Item.class);
			Predicate p = cb.isMember(root.get(Item_.bundle), cb.literal(bundles));
			String[] paths = StringUtils.split(selectEntry.path, ".");
			List<Order> ol = new ArrayList<>();
			// oracle 将空字符串自动转换成null,需要同时判断null和空字符串
			if ((paths.length > 0) && StringUtils.isNotEmpty(paths[0])) {
				p = cb.and(p, cb.equal(root.get(Item_.path0), paths[0]));
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path0)), cb.equal(root.get(Item_.path0), "")));
			}
			if ((paths.length > 1) && StringUtils.isNotEmpty(paths[1])) {
				if (!FilterEntry.WILDCARD.equals(paths[1])) {
					p = cb.and(p, cb.equal(root.get(Item_.path1), paths[1]));
				} else {
					ol.add(cb.asc(root.get(Item_.path1)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path1)), cb.equal(root.get(Item_.path1), "")));
			}
			if ((paths.length > 2) && StringUtils.isNotEmpty(paths[2])) {
				if (!FilterEntry.WILDCARD.equals(paths[2])) {
					p = cb.and(p, cb.equal(root.get(Item_.path2), paths[2]));
				} else {
					ol.add(cb.asc(root.get(Item_.path2)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path2)), cb.equal(root.get(Item_.path2), "")));
			}
			if ((paths.length > 3) && StringUtils.isNotEmpty(paths[3])) {
				if (!FilterEntry.WILDCARD.equals(paths[3])) {
					p = cb.and(p, cb.equal(root.get(Item_.path3), paths[3]));
				} else {
					ol.add(cb.asc(root.get(Item_.path3)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path3)), cb.equal(root.get(Item_.path3), "")));
			}
			if ((paths.length > 4) && StringUtils.isNotEmpty(paths[4])) {
				if (!FilterEntry.WILDCARD.equals(paths[4])) {
					p = cb.and(p, cb.equal(root.get(Item_.path4), paths[4]));
				} else {
					ol.add(cb.asc(root.get(Item_.path4)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path4)), cb.equal(root.get(Item_.path4), "")));
			}
			if ((paths.length > 5) && StringUtils.isNotEmpty(paths[5])) {
				if (!FilterEntry.WILDCARD.equals(paths[5])) {
					p = cb.and(p, cb.equal(root.get(Item_.path5), paths[5]));
				} else {
					ol.add(cb.asc(root.get(Item_.path5)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path5)), cb.equal(root.get(Item_.path5), "")));
			}
			if ((paths.length > 6) && StringUtils.isNotEmpty(paths[6])) {
				if (!FilterEntry.WILDCARD.equals(paths[6])) {
					p = cb.and(p, cb.equal(root.get(Item_.path6), paths[6]));
				} else {
					ol.add(cb.asc(root.get(Item_.path6)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path6)), cb.equal(root.get(Item_.path6), "")));
			}
			if ((paths.length > 7) && StringUtils.isNotEmpty(paths[7])) {
				if (!FilterEntry.WILDCARD.equals(paths[7])) {
					p = cb.and(p, cb.equal(root.get(Item_.path7), paths[7]));
				} else {
					ol.add(cb.asc(root.get(Item_.path7)));
				}
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(Item_.path7)), cb.equal(root.get(Item_.path7), "")));
			}
			cq.multiselect(root.get(Item_.bundle), root.get(Item_.itemPrimitiveType),
					root.get(Item_.itemStringValueType), root.get(Item_.stringShortValue),
					root.get(Item_.stringLongValue), root.get(Item_.dateValue), root.get(Item_.timeValue),
					root.get(Item_.dateTimeValue), root.get(Item_.booleanValue), root.get(Item_.numberValue)).where(p);
			boolean isList = false;
			if (!ol.isEmpty()) {
				isList = true;
				cq.orderBy(ol);
			}
			List<Tuple> list = em.createQuery(cq).getResultList();
			Row row = null;
			Set<String> set = new HashSet<>();
			for (Tuple o : list) {
				set.add(Objects.toString(o.get(0)));
				row = table.get(Objects.toString(o.get(0)));
				switch (ItemPrimitiveType.valueOf(Objects.toString(o.get(1)))) {
				case s:
					switch (ItemStringValueType.valueOf(Objects.toString(o.get(2)))) {
					case s:
						if (null != o.get(3)) {
							if ((null != o.get(4)) && StringUtils.isNotEmpty(Objects.toString(o.get(4)))) {
								row.put(selectEntry.getColumn(), Objects.toString(o.get(4)), isList);
							} else {
								row.put(selectEntry.getColumn(), Objects.toString(o.get(3)), isList);
							}
						}
						break;
					case d:
						if (null != o.get(5)) {
							row.put(selectEntry.getColumn(), JpaObjectTools.confirm((Date) o.get(5)), isList);
						}
						break;
					case t:
						if (null != o.get(6)) {
							row.put(selectEntry.getColumn(), JpaObjectTools.confirm((Date) o.get(6)), isList);
						}
						break;
					case dt:
						if (null != o.get(7)) {
							row.put(selectEntry.getColumn(), JpaObjectTools.confirm((Date) o.get(7)), isList);
						}
						break;
					default:
						break;
					}
					break;
				case b:
					if (null != o.get(8)) {
						row.put(selectEntry.getColumn(), o.get(8), isList);
					}
					break;
				case n:
					if (null != o.get(9)) {
						row.put(selectEntry.getColumn(), o.get(9), isList);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	public static class ExtractObject {

		private Row entry;
		private String bundle;
		private String column;
		private Object value;

		public String getBundle() {
			return bundle;
		}

		public void setBundle(String bundle) {
			this.bundle = bundle;
		}

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Row getEntry() {
			return entry;
		}

		public void setEntry(Row entry) {
			this.entry = entry;
		}

	}

	public byte[] girdToExcel() throws IOException {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			XSSFSheet sheet = workbook.createSheet("grid");
			if ((null != this.grid) && ListTools.isNotEmpty(this.selectList)) {
				List<SelectEntry> selectEntries = this.selectList.stream()
						.filter(o -> BooleanUtils.isNotTrue(o.hideColumn)).collect(Collectors.toList());
				if (ListTools.isNotEmpty(selectEntries)) {
					XSSFRow r = sheet.createRow(0);
					int i = 0;
					for (SelectEntry o : selectEntries) {
						r.createCell(i++).setCellValue(o.getDisplayName());
					}
					Row row = null;
					for (int j = 0; j < this.grid.size(); j++) {
						row = this.grid.get(j);
						r = sheet.createRow(j + 1);
						i = 0;
						for (SelectEntry o : selectEntries) {
							r.createCell(i++).setCellValue(girdToExcelObjectToString(row.find(o.column)));
						}
					}
				}
			}
			workbook.write(os);
			return os.toByteArray();
		}
	}

	private String girdToExcelObjectToString(Object object) {
		String str = "";
		if (object instanceof Integer) {
			str = object.toString();
		} else if (object instanceof Double) {
			str = object.toString();
		} else if (object instanceof Float) {
			str = object.toString();
		} else if (object instanceof Boolean) {
			str = String.valueOf(object);
		} else if (object instanceof Date) {
			str = DateTools.format((Date) object);
		} else if (object instanceof List) {
			str = StringUtils.join((List<?>) object, ",");
		} else {
			str = object.toString();
		}
		return str;
	}
}

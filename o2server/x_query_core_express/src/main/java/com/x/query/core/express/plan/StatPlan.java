package com.x.query.core.express.plan;

import java.math.RoundingMode;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;

public class StatPlan extends GsonPropertyObject {

	private static Logger logger = LoggerFactory.getLogger(StatPlan.class);

	public StatPlan(EntityManagerContainer emc, Stat stat, Runtime runtime) {
		this.calculate = XGsonBuilder.instance().fromJson(stat.getData(), StatPlan.class).getCalculate();
		this.runtime = runtime;
		this.emc = emc;
		this.gson = XGsonBuilder.instance();
	}

	public void access() throws Exception {
		Map<String, Plan> plans = new HashMap<>();
		if (BooleanUtils.isTrue(calculate.isGroup)) {
			this.findGroupPlan(plans);
		} else {
			this.findPlan(plans);
		}
		/* 添加运行时状态 */
		plans.values().forEach(o -> {
			o.runtime = runtime;
		});
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		plans.values().stream().forEach(o -> {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					o.access();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			futures.add(future);
		});
		for (CompletableFuture<Void> future : futures) {
			future.get(300, TimeUnit.SECONDS);
		}
		if (BooleanUtils.isTrue(calculate.isGroup)) {
			this.setCalculateGrid(this.mergeGroup(plans));
		} else {
			this.setCalculateGrid(this.merge(plans));
		}
	}

	private Calculate calculate;

	private Gson gson;

	private EntityManagerContainer emc;

	private Runtime runtime;

	private List<?> calculateGrid = new ArrayList<>();

	public Calculate getCalculate() {
		return calculate;
	}

	public void setCalculate(Calculate calculate) {
		this.calculate = calculate;
	}

	public List<?> getCalculateGrid() {
		return calculateGrid;
	}

	public void setCalculateGrid(List<?> calculateGrid) {
		this.calculateGrid = calculateGrid;
	}

	private void findGroupPlan(Map<String, Plan> plans) throws Exception {
		calculate.calculateList.stream().forEach(o -> {
			View view;
			try {
				view = emc.find(o.view, View.class);
				if (null != view) {
					if (StringUtils.equals((view.getType()), View.TYPE_CMS)) {
						CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
						if ((null != cmsPlan.group) && cmsPlan.group.available()) {
							List<SelectEntry> uselessSelectList = new ArrayList<>();
							cmsPlan.selectList.stream().forEachOrdered(s -> {
								if ((!(s.groupEntry && s.available())) && (!StringUtils.equals(s.column, o.column))) {
									uselessSelectList.add(s);
								}
							});
							cmsPlan.selectList.removeAll(uselessSelectList);
							plans.put(o.id, cmsPlan);
						}
					} else if (StringUtils.equals((view.getType()), View.TYPE_PROCESSPLATFORM)) {
						ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(),
								ProcessPlatformPlan.class);
						if ((null != processPlatformPlan.group) && processPlatformPlan.group.available()) {
							List<SelectEntry> uselessSelectList = new ArrayList<>();
							processPlatformPlan.selectList.stream().forEachOrdered(s -> {
								if ((!(s.groupEntry && s.available())) && (!StringUtils.equals(s.column, o.column))) {
									uselessSelectList.add(s);
								}
							});
							processPlatformPlan.selectList.removeAll(uselessSelectList);
							plans.put(o.id, processPlatformPlan);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private void findPlan(Map<String, Plan> plans) throws Exception {
		calculate.calculateList.stream().forEach(o -> {
			View view;
			try {
				view = emc.find(o.view, View.class);
				if (null != view) {
					if (StringUtils.equals((view.getType()), View.TYPE_CMS)) {
						CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
						/* 非分类视图或者分类视图都可以 */
						List<SelectEntry> uselessSelectList = new ArrayList<>();
						cmsPlan.selectList.stream().forEachOrdered(s -> {
							if (!StringUtils.equals(s.column, o.column)) {
								uselessSelectList.add(s);
							}
						});
						cmsPlan.selectList.removeAll(uselessSelectList);
						plans.put(o.id, cmsPlan);
					} else if (StringUtils.equals((view.getType()), View.TYPE_PROCESSPLATFORM)) {
						ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(),
								ProcessPlatformPlan.class);
						/* 非分类视图或者分类视图都可以 */
						List<SelectEntry> uselessSelectList = new ArrayList<>();
						processPlatformPlan.selectList.stream().forEachOrdered(s -> {
							if (!StringUtils.equals(s.column, o.column)) {
								uselessSelectList.add(s);
							}
						});
						processPlatformPlan.selectList.removeAll(uselessSelectList);
						plans.put(o.id, processPlatformPlan);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
	}

	private CalculateGroupTable mergeGroup(Map<String, Plan> plans) throws Exception {
		CalculateGroupTable table = new CalculateGroupTable();
		final List<Object> keys = new ArrayList<>();
		/* 计算group值 */
		for (Entry<String, Plan> en : plans.entrySet()) {
			logger.debug("merge group plan:{}.", en.getValue());
			if ((null != en.getValue()) && (null != en.getValue().groupGrid)) {
				en.getValue().groupGrid.stream().forEach(o -> {
					keys.add(o.group);
				});
				if (ListTools.isNotEmpty(calculate.groupSpecifiedList)) {
					if (StringUtils.equals(Calculate.GROUPMERGETYPE_SPECIFIED, calculate.groupMergeType)) {
						keys.clear();
						keys.addAll(calculate.groupSpecifiedList);
						logger.debug("group {}:{}.", Calculate.GROUPMERGETYPE_SPECIFIED, keys);
					} else if (StringUtils.equals(Calculate.GROUPMERGETYPE_INTERSECTION, calculate.groupMergeType)) {
						List<Object> os = ListUtils.intersection(keys, calculate.groupSpecifiedList);
						keys.clear();
						keys.addAll(os);
						logger.debug("group {}:{}.", Calculate.GROUPMERGETYPE_INTERSECTION, keys);
					} else if (StringUtils.equals(Calculate.GROUPMERGETYPE_SUM, calculate.groupMergeType)) {
						List<Object> os = ListUtils.sum(keys, calculate.groupSpecifiedList);
						keys.clear();
						keys.addAll(os);
						logger.debug("group {}:{}.", Calculate.GROUPMERGETYPE_SUM, keys);
					}
				}
			}
		}
		/* 填充初始数值 */
		keys.stream().distinct().forEach(o -> {
			CalculateGroupRow row = new CalculateGroupRow();
			row.group = o;
			for (CalculateEntry c : calculate.calculateList) {
				NumberFormat numberFormat = this.getNumberFormat(c);
				CalculateCell cell = new CalculateCell();
				cell.column = c.id;
				cell.displayName = c.displayName;
				cell.value = numberFormat.format((c.defaultValue == null ? 0d : c.defaultValue));
				row.list.add(cell);
			}
			table.add(row);
		});
		logger.debug("init group calculateGrid table:{}.", table);
		for (Entry<String, Plan> en : plans.entrySet()) {
			if ((null != en.getValue().group) && en.getValue().group.available()) {
				en.getValue().selectList.stream().forEach(o -> {
					/* 分类统计只能统计分类视图 */
					if (!StringUtils.equals(o.column, en.getValue().group.column)) {
						CalculateEntry calculateEntry = calculate.get(en.getKey());
						if (null != calculateEntry) {
							NumberFormat numberFormat = this.getNumberFormat(calculateEntry);
							en.getValue().groupGrid.stream().forEach(r -> {
								List<Double> values = new ArrayList<>();
								CalculateGroupRow row = table.getRow(r.group);
								if (null != row) {
									CalculateCell cell = row.getCell(calculateEntry.id);
									if (null != cell) {
										r.list.stream().forEach(c -> {
											values.add(c.getAsDouble(o.column));
										});
										/* 如果有添加的分类值,有可能取得的是null */
										if (StringUtils.equals(calculateEntry.calculateType, Plan.CALCULATE_AVERAGE)) {
											cell.value = numberFormat
													.format(values.stream().mapToDouble(d -> d).average().orElse(0));
										} else if (StringUtils.equals(calculateEntry.calculateType,
												Plan.CALCULATE_SUM)) {
											cell.value = numberFormat.format(values.stream().mapToDouble(d -> d).sum());
										} else {
											cell.value = numberFormat.format(values.stream().count());
										}
									}
								}
							});
						}
					}
				});
			}
		}
		if (StringUtils.equalsIgnoreCase(SelectEntry.ORDER_DESC, calculate.orderType)
				|| StringUtils.equalsIgnoreCase(SelectEntry.ORDER_ASC, calculate.orderType)) {
			/* 需要进行排序如果为空则对标题进行排序 */
			if (StringUtils.isEmpty(calculate.orderColumn)) {
				/* 按分类值进行排序 */
				if (StringUtils.equalsIgnoreCase(SelectEntry.ORDER_ASC, calculate.orderType)) {
					table.sort(new GroupComparator());
				} else {
					table.sort(new GroupComparator().reversed());
				}
			} else {
				if (StringUtils.equalsIgnoreCase(SelectEntry.ORDER_ASC, calculate.orderType)) {
					table.sort(new ColumnComparator(calculate.orderColumn));
				} else {
					table.sort(new ColumnComparator(calculate.orderColumn).reversed());
				}
			}
		}
		return table;
	}

	private CalculateRow merge(Map<String, Plan> plans) throws Exception {
		CalculateRow row = new CalculateRow();
		for (CalculateEntry c : calculate.calculateList) {
			NumberFormat numberFormat = this.getNumberFormat(c);
			CalculateCell cell = new CalculateCell();
			cell.column = c.id;
			cell.displayName = c.displayName;
			cell.value = numberFormat.format((c.defaultValue == null ? 0d : c.defaultValue));
			row.add(cell);
		}
		for (Entry<String, Plan> en : plans.entrySet()) {
			if ((null == en.getValue().group) || (!en.getValue().group.available())) {
				/* 分类视图非分类统计 */
				en.getValue().selectList.stream().forEach(o -> {
					// if (!StringUtils.equals(o.column, en.getValue().group.column)) {
					List<Double> values = new ArrayList<>();
					CalculateEntry calculateEntry = calculate.get(en.getKey());
					NumberFormat numberFormat = this.getNumberFormat(calculateEntry);
					if ((null != calculateEntry) && (null != en.getValue().groupGrid)) {
						en.getValue().groupGrid.stream().forEach(r -> {
							CalculateCell cell = row.getCell(calculateEntry.id);
							if (null != cell) {
								r.list.stream().forEach(c -> {
									values.add(c.getAsDouble(o.column));
								});
								if (StringUtils.equals(calculateEntry.calculateType, Plan.CALCULATE_AVERAGE)) {
									cell.value = numberFormat
											.format(values.stream().mapToDouble(d -> d).average().orElse(0));
								} else if (StringUtils.equals(calculateEntry.calculateType, Plan.CALCULATE_SUM)) {
									cell.value = numberFormat.format(values.stream().mapToDouble(d -> d).sum());
								} else {
									cell.value = numberFormat.format(values.stream().count());
								}
							}
						});
					}
					// }
				});
			} else if ((null != en.getValue()) && (null != en.getValue().grid)) {
				/* 非分类视图非分类统计 */
				en.getValue().selectList.stream().forEach(o -> {
					List<Double> values = new ArrayList<>();
					CalculateEntry calculateEntry = calculate.get(en.getKey());
					NumberFormat numberFormat = this.getNumberFormat(calculateEntry);
					if (null != calculateEntry) {
						en.getValue().grid.stream().forEach(r -> {
							values.add(r.getAsDouble(o.column));
							CalculateCell cell = row.getCell(calculateEntry.id);
							if (StringUtils.equals(calculateEntry.calculateType, Plan.CALCULATE_AVERAGE)) {
								cell.value = numberFormat
										.format(values.stream().mapToDouble(d -> d).average().orElse(0));
							} else if (StringUtils.equals(calculateEntry.calculateType, Plan.CALCULATE_SUM)) {
								cell.value = numberFormat.format(values.stream().mapToDouble(d -> d).sum());
							} else {
								cell.value = numberFormat.format(values.stream().count());
							}
						});
					}
				});
			}
		}
		return row;
	}

	private NumberFormat getNumberFormat(CalculateEntry calculateEntry) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setRoundingMode(RoundingMode.HALF_UP);
		if (StringUtils.equals(calculateEntry.formatType, CalculateEntry.FORMATTYPE_CURRENCY)) {
			numberFormat = NumberFormat.getCurrencyInstance();

			return numberFormat;
		} else if (StringUtils.equals(calculateEntry.formatType, CalculateEntry.FORMATTYPE_PERCENT)) {
			numberFormat = NumberFormat.getPercentInstance();
		}
		if ((null != calculateEntry.decimal) && (calculateEntry.decimal >= 0)) {
			numberFormat.setMaximumFractionDigits(calculateEntry.decimal);
		}
		return numberFormat;
	}

	public static class GroupComparator implements Comparator<CalculateGroupRow> {

		Collator collator = Collator.getInstance(java.util.Locale.CHINA);

		public int compare(CalculateGroupRow r1, CalculateGroupRow r2) {
			return collator.compare(Objects.toString(r1.group, ""), Objects.toString(r2.group, ""));
		}
	}

	public static class ColumnComparator implements Comparator<CalculateGroupRow> {

		Collator collator = Collator.getInstance(java.util.Locale.CHINA);

		private String column;

		public ColumnComparator(String column) {
			this.column = column;
		}

		public int compare(CalculateGroupRow r1, CalculateGroupRow r2) {
			Object o1 = r1.getCell(column).value;
			Object o2 = r2.getCell(column).value;
			if ((o1 instanceof Number) && (o2 instanceof Number)) {
				Double d1 = ((Number) o1).doubleValue();
				Double d2 = ((Number) o2).doubleValue();
				return d1.compareTo(d2);
			} else {
				return collator.compare(Objects.toString(o1, ""), Objects.toString(o2, ""));
			}
		}
	}
}

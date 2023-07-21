package com.x.query.core.express.plan;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class CalculateEntry extends GsonPropertyObject {

	@FieldDescribe("视图")
	public String view;

	@FieldDescribe("统计列")
	public String column;

	@FieldDescribe("统计类型,sum,count,average")
	public String calculateType;

	@FieldDescribe("显示名称")
	public String displayName;

	@FieldDescribe("排序,original,asc,desc")
	public String orderType;

	@FieldDescribe("格式类型:number,percent,currency")
	public String formatType;

	@FieldDescribe("空值的默认值")
	public Double defaultValue;

	@FieldDescribe("小数点位数")
	public Integer decimal;

	@FieldDescribe("用于标识的id,并不是数据库id")
	public String id;

	public static final String FORMATTYPE_NUMBER = "number";
	public static final String FORMATTYPE_PERCENT = "percent";
	public static final String FORMATTYPE_CURRENCY = "currency";

	public Boolean available() {
		if (StringUtils.isEmpty(this.column)) {
			return false;
		}
		if (null == this.orderType) {
			return false;
		}
		return true;
	}

	protected Long count(Table table) throws Exception {
		Long result = table.stream().count();
		return result;
	}

	protected Double sum(Table table) throws Exception {
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(this.column)).sum();
		return result;
	}

	protected Double average(Table table) throws Exception {
		DoubleSupplier ds = () -> {
			return 0d;
		};
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(this.column)).average().orElseGet(ds);
		return result;
	}

	protected Map<Object, Long> groupCount(Table table, SelectEntry groupSelectEntry) throws Exception {
		Map<Object, Long> map = new TreeMap<Object, Long>();
		if ((null != groupSelectEntry) && (groupSelectEntry.available())) {
			map = table.stream()
					.collect(Collectors.groupingBy(row -> row.find(groupSelectEntry.column), Collectors.counting()));
		}
		return map;
	}

	protected Map<Object, Double> groupSum(Table table, SelectEntry groupSelectEntry) throws Exception {
		Map<Object, Double> map = table.stream().collect(Collectors.groupingBy(row -> row.find(groupSelectEntry.column),
				Collectors.summingDouble(row -> row.getAsDouble(this.column))));
		return map;
	}

	protected Map<Object, Double> groupAverage(List<Row> rows, SelectEntry groupSelectEntry) throws Exception {
		Map<Object, Double> map = rows.stream().collect(Collectors.groupingBy(row -> row.find(groupSelectEntry.column),
				Collectors.averagingDouble(row -> row.getAsDouble(this.column))));
		return map;
	}

}
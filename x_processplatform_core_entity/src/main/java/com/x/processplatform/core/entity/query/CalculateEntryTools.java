package com.x.processplatform.core.entity.query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class CalculateEntryTools {

	public static CalculateRow calculate(Table table, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		switch (calculateEntry.getCalculateType()) {
		case count:
			return count(table, calculateEntry);
		case sum:
			return sum(table, calculateEntry);
		case average:
			return average(table, calculateEntry);
		case groupCount:
			return groupCount(table, calculateEntry, groupEntry);
		case groupSum:
			return groupSum(table, calculateEntry, groupEntry);
		case groupAverage:
			return groupAverage(table, calculateEntry, groupEntry);
		default:
			return null;
		}
	}

	private static CalculateRow count(Table table, CalculateEntry calculateEntry) throws Exception {
		Long result = table.stream().count();
		CalculateRow o = new CalculateRow();
		o.setColumn(calculateEntry.getColumn());
		o.setDisplayName(calculateEntry.getDisplayName());
		o.setValue(result);
		return o;
	}

	private static CalculateRow sum(Table table, CalculateEntry calculateEntry) throws Exception {
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(calculateEntry.getColumn())).sum();
		CalculateRow o = new CalculateRow();
		o.setColumn(calculateEntry.getColumn());
		o.setDisplayName(calculateEntry.getDisplayName());
		o.setValue(result);
		return o;
	}

	private static CalculateRow average(Table table, CalculateEntry calculateEntry) throws Exception {
		DoubleSupplier ds = () -> {
			return 0d;
		};
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(calculateEntry.getColumn())).average()
				.orElseGet(ds);
		CalculateRow o = new CalculateRow();
		o.setColumn(calculateEntry.getColumn());
		o.setDisplayName(calculateEntry.getDisplayName());
		o.setValue(result);
		return o;
	}

	private static CalculateRow groupCount(Table table, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		CalculateRow o = new CalculateRow();
		o.setColumn(calculateEntry.getColumn());
		o.setDisplayName(calculateEntry.getDisplayName());
		if ((null == groupEntry) || (!groupEntry.available())) {
			o.setValue(null);
		} else {
			Map<Object, Long> map = table.stream()
					.collect(Collectors.groupingBy(row -> row.get(groupEntry.getColumn()), Collectors.counting()));
			LinkedHashMap<Object, Long> linkedHashMap = new LinkedHashMap<>(map);
			if ((!Objects.equals(OrderType.original, calculateEntry.getOrderType()))
					&& (null != calculateEntry.getOrderEffectType())) {
				linkedHashMap = sortGroup(linkedHashMap, calculateEntry);
			}
			o.setValue(linkedHashMap);
		}
		return o;
	}

	private static CalculateRow groupSum(List<Row> rows, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		CalculateRow o = new CalculateRow();
		o.setColumn(calculateEntry.getColumn());
		o.setDisplayName(calculateEntry.getDisplayName());
		if ((null == groupEntry) || (!groupEntry.available())) {
			o.setValue(null);
		} else {
			Map<Object, Double> map = rows.stream()
					.collect(Collectors.groupingBy(row -> row.get(groupEntry.getColumn()),
							Collectors.summingDouble(row -> row.getAsDouble(calculateEntry.getColumn()))));
			LinkedHashMap<Object, Double> linkedHashMap = new LinkedHashMap<>(map);
			if ((!Objects.equals(OrderType.original, calculateEntry.getOrderType()))
					&& (null != calculateEntry.getOrderEffectType())) {
				linkedHashMap = sortGroup(linkedHashMap, calculateEntry);
			}
			o.setValue(linkedHashMap);
		}
		return o;
	}

	private static CalculateRow groupAverage(List<Row> rows, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		CalculateRow o = new CalculateRow();
		o.setColumn(calculateEntry.getColumn());
		o.setDisplayName(calculateEntry.getDisplayName());
		if ((null == groupEntry) || (!groupEntry.available())) {
			o.setValue(null);
		} else {
			Map<Object, Double> map = rows.stream()
					.collect(Collectors.groupingBy(row -> row.get(groupEntry.getColumn()),
							Collectors.averagingDouble(row -> row.getAsDouble(calculateEntry.getColumn()))));
			LinkedHashMap<Object, Double> linkedHashMap = new LinkedHashMap<>(map);
			if ((!Objects.equals(OrderType.original, calculateEntry.getOrderType()))
					&& (null != calculateEntry.getOrderEffectType())) {
				linkedHashMap = sortGroup(linkedHashMap, calculateEntry);
			}
			o.setValue(linkedHashMap);
		}
		return o;
	}

	private static <T extends Number> LinkedHashMap<Object, T> sortGroup(LinkedHashMap<Object, T> linkedHashMap,
			CalculateEntry calculateEntry) {
		return linkedHashMap.entrySet().stream().sorted(
				(e1, e2) -> compareWith(e1, e2, calculateEntry.getOrderEffectType(), calculateEntry.getOrderType()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static int compareWith(Entry<Object, ? extends Number> e1, Entry<Object, ? extends Number> e2,
			OrderEffectType orderEffectType, OrderType orderType) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		switch (orderType) {
		case asc:
			switch (orderEffectType) {
			case key:
				compareToBuilder.append(e1.getKey(), e2.getKey());
				break;
			case value:
				compareToBuilder.append(e1.getValue(), e2.getValue());
				break;
			default:
				break;
			}
			break;
		case desc:
			switch (orderEffectType) {
			case key:
				compareToBuilder.append(e2.getKey(), e1.getKey());
				break;
			case value:
				compareToBuilder.append(e2.getValue(), e1.getValue());
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return compareToBuilder.toComparison();
	}
}

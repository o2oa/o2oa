package com.x.cms.core.entity.query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class CalculateEntryTools {

	public static Object calculate(List<Row> rows, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		switch (calculateEntry.getCalculateType()) {
		case count:
			return count(rows, calculateEntry);
		case sum:
			return sum(rows, calculateEntry);
		case average:
			return average(rows, calculateEntry);
		case groupCount:
			return groupCount(rows, calculateEntry, groupEntry);
		case groupSum:
			return groupSum(rows, calculateEntry, groupEntry);
		case groupAverage:
			return groupAverage(rows, calculateEntry, groupEntry);
		default:
			return null;
		}
	}

	private static Long count(List<Row> rows, CalculateEntry calculateEntry) throws Exception {
		Long result = rows.stream().count();
		return result;
	}

	private static Double sum(List<Row> rows, CalculateEntry calculateEntry) throws Exception {
		Double result = rows.stream().mapToDouble(row -> row.getAsDouble(calculateEntry.getColumn())).sum();
		return result;
	}

	private static Double average(List<Row> rows, CalculateEntry calculateEntry) throws Exception {
		DoubleSupplier ds = () -> {
			return 0d;
		};
		return rows.stream().mapToDouble(row -> row.getAsDouble(calculateEntry.getColumn())).average().orElseGet(ds);
	}

	private static LinkedHashMap<Object, Long> groupCount(List<Row> rows, CalculateEntry calculateEntry,
			GroupEntry groupEntry) throws Exception {
		if ((null == groupEntry) || (!groupEntry.available())) {
			return null;
		}
		Map<Object, Long> map = rows.stream()
				.collect(Collectors.groupingBy(row -> row.get(groupEntry.getColumn()), Collectors.counting()));
		LinkedHashMap<Object, Long> linkedHashMap = new LinkedHashMap<>(map);
		if ((!Objects.equals(OrderType.original, calculateEntry.getOrderType()))
				&& (null != calculateEntry.getOrderEffectType())) {
			linkedHashMap = sortGroup(linkedHashMap, calculateEntry);
		}
		return linkedHashMap;
	}

	private static LinkedHashMap<Object, Double> groupSum(List<Row> rows, CalculateEntry calculateEntry,
			GroupEntry groupEntry) throws Exception {
		if ((null == groupEntry) || (!groupEntry.available())) {
			return null;
		}
		Map<Object, Double> map = rows.stream().collect(Collectors.groupingBy(row -> row.get(groupEntry.getColumn()),
				Collectors.summingDouble(row -> row.getAsDouble(calculateEntry.getColumn()))));
		LinkedHashMap<Object, Double> linkedHashMap = new LinkedHashMap<>(map);
		if ((!Objects.equals(OrderType.original, calculateEntry.getOrderType()))
				&& (null != calculateEntry.getOrderEffectType())) {
			linkedHashMap = sortGroup(linkedHashMap, calculateEntry);
		}
		return linkedHashMap;
	}

	private static LinkedHashMap<Object, Double> groupAverage(List<Row> rows, CalculateEntry calculateEntry,
			GroupEntry groupEntry) throws Exception {
		if ((null == groupEntry) || (!groupEntry.available())) {
			return null;
		}
		Map<Object, Double> map = rows.stream().collect(Collectors.groupingBy(row -> row.get(groupEntry.getColumn()),
				Collectors.averagingDouble(row -> row.getAsDouble(calculateEntry.getColumn()))));
		LinkedHashMap<Object, Double> linkedHashMap = new LinkedHashMap<>(map);
		if ((!Objects.equals(OrderType.original, calculateEntry.getOrderType()))
				&& (null != calculateEntry.getOrderEffectType())) {
			linkedHashMap = sortGroup(linkedHashMap, calculateEntry);
		}
		return linkedHashMap;
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

package com.x.cms.core.entity.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public class CalculateEntryTools {

	public static List<CalculateCell> calculateAmount(Table table, List<CalculateEntry> calculateEntries)
			throws Exception {
		List<CalculateCell> list = new ArrayList<>();
		for (CalculateEntry o : calculateEntries) {
			switch (o.getCalculateType()) {
			case count:
				list.add(new CalculateCell(o, count(table, o)));
				break;
			case sum:
				list.add(new CalculateCell(o, sum(table, o)));
				break;
			case average:
				list.add(new CalculateCell(o, average(table, o)));
				break;
			default:
				break;
			}
		}
		return list;
	}

	public static List<?> calculate(Table table, Calculate calculate, GroupEntry groupEntry) throws Exception {
		List<Object> list = new ArrayList<>();
		if (BooleanUtils.isTrue(calculate.getIsGroup())) {
			LinkedHashMap<Object, List<CalculateCell>> map = new LinkedHashMap<>();
			for (int i = 0; i < calculate.getCalculateEntryList().size(); i++) {
				CalculateEntry calculateEntry = calculate.getCalculateEntryList().get(i);
				Map<Object, ? extends Number> m = groupCalcluateEntry(table, calculateEntry, groupEntry);
				if (i == 0) {
					for (Entry<Object, ? extends Number> en : m.entrySet()) {
						List<CalculateCell> row = new ArrayList<>();
						row.add(new CalculateCell(calculateEntry, en.getValue()));
						map.put(en.getKey(), row);
					}
				} else {
					for (Entry<Object, ? extends Number> en : m.entrySet()) {
						map.get(en.getKey()).add(new CalculateCell(calculateEntry, en.getValue()));
					}
				}
			}
			for (Entry<Object, List<CalculateCell>> en : map.entrySet()) {
				LinkedHashMap<String, Object> o = new LinkedHashMap<>();
				o.put("group", en.getKey());
				o.put("list", en.getValue());
				list.add(o);
			}
		} else {
			for (CalculateEntry o : calculate.getCalculateEntryList()) {
				switch (o.getCalculateType()) {
				case count:
					list.add(new CalculateCell(o, count(table, o)));
					break;
				case sum:
					list.add(new CalculateCell(o, sum(table, o)));
					break;
				case average:
					list.add(new CalculateCell(o, average(table, o)));
					break;
				default:
					break;
				}
			}
		}
		return list;
	}

	private static Map<Object, ? extends Number> groupCalcluateEntry(Table table, CalculateEntry calculateEntry,
			GroupEntry groupEntry) throws Exception {
		switch (calculateEntry.getCalculateType()) {
		case count:
			return groupCount(table, calculateEntry, groupEntry);
		case sum:
			return groupSum(table, calculateEntry, groupEntry);
		case average:
			return groupAverage(table, calculateEntry, groupEntry);
		default:
			return new LinkedHashMap<Object, Number>();
		}
	}

	private static Long count(Table table, CalculateEntry calculateEntry) throws Exception {
		Long result = table.stream().count();
		return result;
	}

	private static Double sum(Table table, CalculateEntry calculateEntry) throws Exception {
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(calculateEntry.getColumn())).sum();
		return result;
	}

	private static Double average(Table table, CalculateEntry calculateEntry) throws Exception {
		DoubleSupplier ds = () -> {
			return 0d;
		};
		Double result = table.stream().mapToDouble(row -> row.getAsDouble(calculateEntry.getColumn())).average()
				.orElseGet(ds);
		return result;
	}

	private static Map<Object, Long> groupCount(Table table, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		Map<Object, Long> map = new LinkedHashMap<Object, Long>();
		if ((null != groupEntry) && (groupEntry.available())) {
			map = table.stream()
					.collect(Collectors.groupingBy(row -> row.find(groupEntry.getColumn()), Collectors.counting()));
		}
		return map;
	}

	private static Map<Object, Double> groupSum(Table table, CalculateEntry calculateEntry, GroupEntry groupEntry)
			throws Exception {
		Map<Object, Double> map = table.stream().collect(Collectors.groupingBy(row -> row.find(groupEntry.getColumn()),
				Collectors.summingDouble(row -> row.getAsDouble(calculateEntry.getColumn()))));
		return map;
	}

	private static Map<Object, Double> groupAverage(List<Row> rows, CalculateEntry calculateEntry,
			GroupEntry groupEntry) throws Exception {
		Map<Object, Double> map = rows.stream().collect(Collectors.groupingBy(row -> row.find(groupEntry.getColumn()),
				Collectors.averagingDouble(row -> row.getAsDouble(calculateEntry.getColumn()))));
		return map;
	}

	@SuppressWarnings("unused")
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

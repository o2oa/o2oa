package com.x.cms.core.entity.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class GroupEntryTools {
	public static List<LinkedHashMap<String, Object>> group(Table table, GroupEntry groupEntry,
			List<OrderEntry> orderEntries) throws Exception {
		Map<Object, List<Row>> map = table.stream()
				.collect(Collectors.groupingBy(row -> row.find(groupEntry.getColumn())));
		List<LinkedHashMap<String, Object>> result = new ArrayList<>();

		for (Entry<Object, List<Row>> en : map.entrySet()) {
			Table o = new Table();
			o.addAll(en.getValue());
			LinkedHashMap<String, Object> m = new LinkedHashMap<>();
			m.put("group", en.getKey());
			m.put("list", OrderEntryTools.order(o, orderEntries));
			// m.put("list", o);
			result.add(m);
		}
		/* 分类值再进行一次排序 */
		if (!Objects.equals(OrderType.original, groupEntry.getOrderType())) {
			result = result.stream().sorted((e1, e2) -> compareWith(e1, e2, groupEntry)).collect(Collectors.toList());
			// .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
			// (e1, e2) -> e1, LinkedHashMap::new));
		}
		return result;
	}

	public static int compareWith(LinkedHashMap<String, Object> m1, LinkedHashMap<String, Object> m2,
			GroupEntry groupEntry) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		if (Objects.equals(OrderType.asc, groupEntry.getOrderType())) {
			compareToBuilder.append(m1.get("group"), m2.get("group"));
		} else {
			compareToBuilder.append(m2.get("group"), m1.get("group"));
		}
		return compareToBuilder.toComparison();
	}
}

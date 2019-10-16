package com.x.processplatform.core.express.query;

import java.util.List;
import java.util.stream.Collectors;

import com.x.processplatform.core.entity.query.OrderType;
import com.x.processplatform.core.entity.query.SelectEntry;
import com.x.processplatform.core.entity.query.SelectType;

public class SelectEntryTools {

	public static List<SelectEntry> filterOrderSelectEntries(List<SelectEntry> selectEntries) {
		List<SelectEntry> list = selectEntries.stream().filter(s -> (!OrderType.original.equals(s.getOrderType())))
				.sorted((o1, o2) -> Integer.compare(o1.getOrderRank(), o2.getOrderRank())).collect(Collectors.toList());
		return list;
	}

	public static List<SelectEntry> filterAttributeSelectEntries(List<SelectEntry> selectEntries) {
		List<SelectEntry> list = selectEntries.stream()
				.filter(s -> SelectType.attribute.equals(s.getSelectType())).collect(Collectors.toList());
		return list;
	}

	public static List<SelectEntry> filterPathSelectEntries(List<SelectEntry> selectEntries) {
		List<SelectEntry> list = selectEntries.stream().filter(s -> SelectType.path.equals(s.getSelectType()))
				.collect(Collectors.toList());
		return list;
	}

}

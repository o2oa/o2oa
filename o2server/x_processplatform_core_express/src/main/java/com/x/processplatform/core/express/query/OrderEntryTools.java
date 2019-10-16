package com.x.processplatform.core.express.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.query.OrderEntry;
import com.x.processplatform.core.entity.query.OrderType;
import com.x.processplatform.core.entity.query.Row;
import com.x.processplatform.core.entity.query.Table;

public class OrderEntryTools {

	public static Table order(Table table, List<OrderEntry> orderEntries) {
		List<Row> list = new ArrayList<>();
		if ((null != table) && (!table.isEmpty())) {
			if (ListTools.isNotEmpty(orderEntries)) {
				list = table.stream().sorted((o1, o2) -> compareWith(o1, o2, orderEntries))
						.collect(Collectors.toList());
				Table o = new Table();
				o.addAll(list);
				return o;
			}
		}
		return table;
	}

	public static int compareWith(Row o1, Row o2, List<OrderEntry> orderEntries) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		for (OrderEntry en : orderEntries) {
			if (Objects.equals(OrderType.asc, en.getOrderType())) {
				compareToBuilder.append(o1.find(en.getColumn()), o2.find(en.getColumn()));
			} else {
				compareToBuilder.append(o2.find(en.getColumn()), o1.find(en.getColumn()));
			}
		}
		return compareToBuilder.toComparison();
	}

}

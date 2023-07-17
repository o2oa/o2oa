package com.x.query.core.express.plan;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class TableRowComparator implements Comparator<Row> {

	private final Collator collator = Collator.getInstance(Locale.CHINESE);

	public TableRowComparator(List<SelectEntry> orderList) {
		Objects.requireNonNull(orderList);
		this.orderList = orderList;
	}

	private List<SelectEntry> orderList;

	public int compare(Row r1, Row r2) {
		return orderList.stream().map(o -> compareRow(o, r1, r2)).filter(o -> o != 0).findFirst().orElse(0);
	}

	private int compareRow(SelectEntry en, Row r1, Row r2) {
		int comp;
		Object o1;
		Object o2;
		o1 = r1.find(en.column);
		o2 = r2.find(en.column);
		if (BooleanUtils.isTrue(en.numberOrder)) {
			comp = compareNumber(en, o1, o2);
		} else if (null == o1 && null == o2) {
			comp = 0;
		} else if (null == o1) {
			comp = -1;
		} else if (null == o2) {
			comp = 1;
		} else if (o1 instanceof Collection<?> || o2 instanceof Collection<?>) {
			comp = 0;
		} else {
			comp = compare(en, o1, o2);
		}
		return comp;
	}

	private int compare(SelectEntry en, Object o1, Object o2) {
		if ((o1 instanceof CharSequence) && (o2 instanceof CharSequence)) {
			return StringUtils.equals(SelectEntry.ORDER_ASC, en.orderType) ? collator.compare(o1, o2)
					: collator.compare(o2, o1);
		}
		Comparable c1;
		Comparable c2;
		if (o1.getClass() == o2.getClass()) {
			c1 = (Comparable) o1;
			c2 = (Comparable) o2;
		} else {
			c1 = o1.toString();
			c2 = o2.toString();
		}
		return StringUtils.equals(SelectEntry.ORDER_ASC, en.orderType) ? c1.compareTo(c2) : c2.compareTo(c1);
	}

	private int compareNumber(SelectEntry en, Object o1, Object o2) {
		Double c1 = toNumber(o1);
		Double c2 = toNumber(o2);
		return StringUtils.equals(SelectEntry.ORDER_ASC, en.orderType) ? c1.compareTo(c2) : c2.compareTo(c1);
	}

	private Double toNumber(Object o) {
		Double d;
		if ((null == o) || StringUtils.isEmpty(o.toString())) {
			d = Double.MAX_VALUE;
		} else {
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				d = Double.MAX_VALUE;
			}
		}
		return d;
	}
}
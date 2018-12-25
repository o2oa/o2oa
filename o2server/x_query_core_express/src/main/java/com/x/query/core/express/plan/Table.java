package com.x.query.core.express.plan;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

public class Table extends TreeList<Row> {

	public Row get(String bundle) throws Exception {
		for (Row o : this) {
			if (StringUtils.equals(o.bundle, bundle)) {
				return o;
			}
		}
		return null;
	}
}
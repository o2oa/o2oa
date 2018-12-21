package com.x.query.core.express.plan;

import java.util.Objects;

import org.apache.commons.collections4.list.TreeList;

public class CalculateGroupTable extends TreeList<CalculateGroupRow> {

	public CalculateGroupRow getRow(Object key) {
		for (CalculateGroupRow row : this) {
			if (Objects.equals(key, row.group)) {
				return row;
			}
		}
		return null;
	}

}
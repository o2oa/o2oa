package com.x.query.core.express.plan;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

public class CalculateRow extends TreeList<CalculateCell> {

	static final long serialVersionUID = -822502434961496476L;

	public CalculateCell getCell(String column) {
		for (CalculateCell cell : this) {
			if (StringUtils.equals(cell.column, column)) {
				return cell;
			}
		}
		return null;
	}
}
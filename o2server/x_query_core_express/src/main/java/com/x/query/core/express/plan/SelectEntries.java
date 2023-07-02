package com.x.query.core.express.plan;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

public class SelectEntries extends TreeList<SelectEntry> {

	public SelectEntry column(String column) {
		for (SelectEntry o : this) {
			if (StringUtils.equals(column, o.column)) {
				return o;
			}
		}
		return null;
	}

	public Boolean emptyColumnCode() {
		for (SelectEntry en : this) {
			if (StringUtils.isNotBlank(en.code)) {
				return false;
			}
		}
		return true;
	}

}

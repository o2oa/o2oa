package com.x.cms.core.entity.query;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

public class Table extends CopyOnWriteArrayList<Row> {

	private static final long serialVersionUID = 7525624451700204922L;

	public Row get(String categoryId) throws Exception {
		for (Row o : this) {
			if (StringUtils.equals(o.getCategoryId(), categoryId )) {
				return o;
			}
		}
		return null;
	}

}
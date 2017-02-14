package com.x.cms.core.entity.query;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

public class Table extends CopyOnWriteArrayList<Row> {

	private static final long serialVersionUID = 7525624451700204922L;

	public Row get(String job) throws Exception {
		for (Row o : this) {
			if (StringUtils.equals(o.getJob(), job)) {
				return o;
			}
		}
		return null;
	}

}
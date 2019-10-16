package com.x.processplatform.core.express.query;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.x.processplatform.core.entity.query.Row;

public class Table extends ArrayList<Row> {

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
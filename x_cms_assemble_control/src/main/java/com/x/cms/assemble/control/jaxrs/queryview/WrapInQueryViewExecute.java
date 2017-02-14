package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.cms.core.entity.query.FilterEntry;
import com.x.cms.core.entity.query.WhereEntry;


public class WrapInQueryViewExecute extends GsonPropertyObject {

	private List<FilterEntry> filterEntryList;

	private WhereEntry whereEntry;

	private List<String> columnList;

	public List<FilterEntry> getFilterEntryList() {
		return filterEntryList;
	}

	public void setFilterEntryList(List<FilterEntry> filterEntryList) {
		this.filterEntryList = filterEntryList;
	}

	public WhereEntry getWhereEntry() {
		return whereEntry;
	}

	public void setWhereEntry(WhereEntry whereEntry) {
		this.whereEntry = whereEntry;
	}

	public List<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<String> columnList) {
		this.columnList = columnList;
	}

}

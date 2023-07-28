package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class DataRecordProperties extends JsonProperties {

	private static final long serialVersionUID = 4917809511933444732L;

	private List<DataRecordItem> dataRecordItemList = new ArrayList<>();

	public List<DataRecordItem> getDataRecordItemList() {
		return dataRecordItemList;
	}

	public void setDataRecordItemList(List<DataRecordItem> dataRecordItemList) {
		this.dataRecordItemList = dataRecordItemList;
	}
}

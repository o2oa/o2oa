package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

public class WrapOutStatistic {
	
	public static List<String> Excludes = new ArrayList<String>();

	private String statisticName = null;
	
	private String sequnce = null;
	
	private String statisticType = null;
	
	private long count = 0;

	public String getStatisticName() {
		return statisticName;
	}
	public void setStatisticName(String statisticName) {
		this.statisticName = statisticName;
	}
	public String getSequnce() {
		return sequnce;
	}
	public void setSequnce(String sequnce) {
		this.sequnce = sequnce;
	}
	public String getStatisticType() {
		return statisticType;
	}
	public void setStatisticType(String statisticType) {
		this.statisticType = statisticType;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JsonProperties;

public class WorkLogProperties extends JsonProperties {

	private static final long serialVersionUID = -4149341123576111783L;

	private List<String> splitTokenList;

	private List<String> splitValueList = new ArrayList<String>();

	private String splitToken;

	private String splitValue;

	public List<String> getSplitTokenList() {
		if (null == splitTokenList) {
			this.splitTokenList = new ArrayList<>();
		}
		return splitTokenList;
	}

	public List<String> getSplitValueList() {
		if (null == splitValueList) {
			this.splitValueList = new ArrayList<String>();
		}
		return splitValueList;
	}

	public void setSplitTokenList(List<String> splitTokenList) {
		this.splitTokenList = splitTokenList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
	}

	public String getSplitToken() {
		return splitToken;
	}

	public void setSplitToken(String splitToken) {
		this.splitToken = splitToken;
	}

	public String getSplitValue() {
		return splitValue;
	}

	public void setSplitValue(String splitValue) {
		this.splitValue = splitValue;
	}

}

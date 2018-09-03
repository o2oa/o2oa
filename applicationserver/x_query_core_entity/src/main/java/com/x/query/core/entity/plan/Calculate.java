package com.x.query.core.entity.plan;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class Calculate extends GsonPropertyObject {

	public Calculate() {
		this.isGroup = false;
		this.calculateList = new TreeList<CalculateEntry>();
	}

	public Boolean isGroup;

	public String orderType;

	public String id;

	public List<CalculateEntry> calculateList;

	public Boolean available() {
		for (CalculateEntry o : ListTools.nullToEmpty(this.calculateList)) {
			if (o.available()) {
				return true;
			}
		}
		return false;
	}

 

}
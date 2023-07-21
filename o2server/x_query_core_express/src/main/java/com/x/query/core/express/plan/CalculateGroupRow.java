package com.x.query.core.express.plan;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CalculateGroupRow extends GsonPropertyObject {

	public Object group = "";

	public List<CalculateCell> list = new TreeList<>();

	public CalculateCell getCell(String column) {
		for (CalculateCell cell : this.list) {
			if (StringUtils.equals(cell.column, column)) {
				return cell;
			}
		}
		return null;
	}

}
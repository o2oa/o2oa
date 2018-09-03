package com.x.query.core.entity.plan;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CalculateGroupRow extends GsonPropertyObject {

	public Object group = "";

	public List<CalculateCell> list = new TreeList<>();

}
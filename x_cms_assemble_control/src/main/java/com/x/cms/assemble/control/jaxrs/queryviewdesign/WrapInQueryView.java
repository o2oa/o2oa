package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.QueryView;


@Wrap(QueryView.class)
public class WrapInQueryView extends QueryView {

	private static final long serialVersionUID = -5237741099036357033L;
	public static List<String> CreateExcludes = new ArrayList<>();
	public static List<String> UpdateExcludes = new ArrayList<>();

	static {
		CreateExcludes.add(DISTRIBUTEFACTOR);
		CreateExcludes.add("updateTime");
		CreateExcludes.add("createTime");
		CreateExcludes.add("sequence");
		CreateExcludes.add("lastUpdatePerson");
		CreateExcludes.add("lastUpdateTime");
	}

	static {
		UpdateExcludes.add(DISTRIBUTEFACTOR);
		UpdateExcludes.add(ID);
		UpdateExcludes.add("updateTime");
		UpdateExcludes.add("createTime");
		UpdateExcludes.add("sequence");
		UpdateExcludes.add("lastUpdatePerson");
		UpdateExcludes.add("lastUpdateTime");
	}

}
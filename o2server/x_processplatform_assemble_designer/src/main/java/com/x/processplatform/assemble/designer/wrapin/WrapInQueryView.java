package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.QueryView;

public class WrapInQueryView extends QueryView {

	private static final long serialVersionUID = -5237741099036357033L;
	public static List<String> CreateExcludes = new ArrayList<>();
	public static List<String> UpdateExcludes = new ArrayList<>();

	static {
		CreateExcludes.add(JpaObject.distributeFactor_FIELDNAME);
		CreateExcludes.add("updateTime");
		CreateExcludes.add("createTime");
		CreateExcludes.add("sequence");
		CreateExcludes.add("lastUpdatePerson");
		CreateExcludes.add("lastUpdateTime");
	}

	static {
		UpdateExcludes.add(JpaObject.distributeFactor_FIELDNAME);
		UpdateExcludes.add(JpaObject.id_FIELDNAME);
		UpdateExcludes.add("updateTime");
		UpdateExcludes.add("createTime");
		UpdateExcludes.add("sequence");
		UpdateExcludes.add("lastUpdatePerson");
		UpdateExcludes.add("lastUpdateTime");
	}

}
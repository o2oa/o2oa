package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.QueryStat;
public class WrapOutQueryStat extends QueryStat {

	private static final long serialVersionUID = 2886873983211744188L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}

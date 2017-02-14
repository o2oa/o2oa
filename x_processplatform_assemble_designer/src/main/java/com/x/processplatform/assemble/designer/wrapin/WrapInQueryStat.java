package com.x.processplatform.assemble.designer.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.QueryStat;

@Wrap(QueryStat.class)
public class WrapInQueryStat extends QueryStat {

	private static final long serialVersionUID = -5237741099036357033L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

	static {
		Excludes.add("lastUpdatePerson");
		Excludes.add("lastUpdateTime");
	}

}
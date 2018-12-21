package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.Stat;

public class WrapStat extends Stat {

	private static final long serialVersionUID = -7289833343220501308L;

	public static WrapCopier<Stat, WrapStat> outCopier = WrapCopierFactory.wo(Stat.class, WrapStat.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapStat, Stat> inCopier = WrapCopierFactory.wi(WrapStat.class, Stat.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

}

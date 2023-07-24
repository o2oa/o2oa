package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Parallel;

public class WrapParallel extends Parallel {

	private static final long serialVersionUID = 75933203079688664L;

	public static final WrapCopier<Parallel, WrapParallel> outCopier = WrapCopierFactory.wo(Parallel.class,
			WrapParallel.class, null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapParallel, Parallel> inCopier = WrapCopierFactory.wi(WrapParallel.class,
			Parallel.class, null, JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}

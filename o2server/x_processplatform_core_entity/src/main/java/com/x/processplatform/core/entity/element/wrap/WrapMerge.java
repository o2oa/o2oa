package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Merge;

public class WrapMerge extends Merge {

	private static final long serialVersionUID = 2668844008956239077L;

	public static final WrapCopier<Merge, WrapMerge> outCopier = WrapCopierFactory.wo(Merge.class, WrapMerge.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapMerge, Merge> inCopier = WrapCopierFactory.wi(WrapMerge.class, Merge.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);

}

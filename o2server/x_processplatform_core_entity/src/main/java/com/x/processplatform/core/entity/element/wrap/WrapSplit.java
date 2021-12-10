package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Split;

public class WrapSplit extends Split {

	private static final long serialVersionUID = 5651378255582879520L;

	public static final WrapCopier<Split, WrapSplit> outCopier = WrapCopierFactory.wo(Split.class, WrapSplit.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapSplit, Split> inCopier = WrapCopierFactory.wi(WrapSplit.class, Split.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}

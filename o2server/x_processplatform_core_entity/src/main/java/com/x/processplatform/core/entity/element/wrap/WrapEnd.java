package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.End;

public class WrapEnd extends End {

	private static final long serialVersionUID = -8770201790121656238L;

	public static final WrapCopier<End, WrapEnd> outCopier = WrapCopierFactory.wo(End.class, WrapEnd.class, null,
			JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapEnd, End> inCopier = WrapCopierFactory.wi(WrapEnd.class, End.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);

}

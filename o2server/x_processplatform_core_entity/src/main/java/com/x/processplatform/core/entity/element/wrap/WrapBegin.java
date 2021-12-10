package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Begin;

public class WrapBegin extends Begin {

	private static final long serialVersionUID = 1183685344142941533L;

	public static final WrapCopier<Begin, WrapBegin> outCopier = WrapCopierFactory.wo(Begin.class, WrapBegin.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapBegin, Begin> inCopier = WrapCopierFactory.wi(WrapBegin.class, Begin.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}

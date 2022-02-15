package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Manual;

public class WrapManual extends Manual {

	private static final long serialVersionUID = 4037202596279188116L;

	public static final WrapCopier<Manual, WrapManual> outCopier = WrapCopierFactory.wo(Manual.class, WrapManual.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapManual, Manual> inCopier = WrapCopierFactory.wi(WrapManual.class, Manual.class,
			null, JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}

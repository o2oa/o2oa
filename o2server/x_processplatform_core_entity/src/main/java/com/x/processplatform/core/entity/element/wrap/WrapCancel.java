package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Cancel;

public class WrapCancel extends Cancel {

	private static final long serialVersionUID = -3049856052285398091L;

	public static final WrapCopier<Cancel, WrapCancel> outCopier = WrapCopierFactory.wo(Cancel.class, WrapCancel.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapCancel, Cancel> inCopier = WrapCopierFactory.wi(WrapCancel.class, Cancel.class,
			null, JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}

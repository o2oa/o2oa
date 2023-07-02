package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Delay;

public class WrapDelay extends Delay {

	private static final long serialVersionUID = 6695709068501511733L;

	public static final WrapCopier<Delay, WrapDelay> outCopier = WrapCopierFactory.wo(Delay.class, WrapDelay.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapDelay, Delay> inCopier = WrapCopierFactory.wi(WrapDelay.class, Delay.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);

}

package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Invoke;

public class WrapInvoke extends Invoke {

	private static final long serialVersionUID = 671190420770471675L;

	public static final WrapCopier<Invoke, WrapInvoke> outCopier = WrapCopierFactory.wo(Invoke.class, WrapInvoke.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapInvoke, Invoke> inCopier = WrapCopierFactory.wi(WrapInvoke.class, Invoke.class,
			null, JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);

}

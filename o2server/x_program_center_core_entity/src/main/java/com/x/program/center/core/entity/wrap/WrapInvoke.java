package com.x.program.center.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Invoke;

public class WrapInvoke extends Invoke {

	private static final long serialVersionUID = 945749702463375818L;

	public static WrapCopier<Invoke, WrapInvoke> outCopier = WrapCopierFactory.wo(Invoke.class, WrapInvoke.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapInvoke, Invoke> inCopier = WrapCopierFactory.wi(WrapInvoke.class, Invoke.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}
package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Script;

public class WrapScript extends Script {

	private static final long serialVersionUID = -144375180428371919L;

	public static final WrapCopier<Script, WrapScript> outCopier = WrapCopierFactory.wo(Script.class, WrapScript.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapScript, Script> inCopier = WrapCopierFactory.wi(WrapScript.class, Script.class,
			null, JpaObject.FieldsUnmodifyExcludeId, false);

}
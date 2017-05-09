package com.x.portal.assemble.surface.jaxrs.script;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.surface.wrapout.WrapOutScript;
import com.x.portal.core.entity.Script;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<Script, WrapOutScript> outCopier = BeanCopyToolsBuilder.create(Script.class,
			WrapOutScript.class, null, WrapOutScript.Excludes);

}

package com.x.processplatform.assemble.designer.jaxrs.script;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.wrapin.WrapInScript;
import com.x.processplatform.assemble.designer.wrapout.WrapOutScript;
import com.x.processplatform.core.entity.element.Script;

class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<Script, WrapOutScript> outCopier = BeanCopyToolsBuilder.create(Script.class,
			WrapOutScript.class, null, WrapOutScript.Excludes);
	static BeanCopyTools<WrapInScript, Script> inCopier = BeanCopyToolsBuilder.create(WrapInScript.class, Script.class,
			null, WrapInScript.Excludes);

}

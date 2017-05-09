package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplicationDict;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDict;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<ApplicationDict, WrapOutApplicationDict> outCopier = BeanCopyToolsBuilder
			.create(ApplicationDict.class, WrapOutApplicationDict.class, null, WrapOutApplicationDict.Excludes);

	static BeanCopyTools<WrapInApplicationDict, ApplicationDict> inCopier = BeanCopyToolsBuilder
			.create(WrapInApplicationDict.class, ApplicationDict.class, null, WrapInApplicationDict.Excludes);

}

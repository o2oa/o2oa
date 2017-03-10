package com.x.organization.assemble.authentication.jaxrs.bind;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.authentication.wrap.out.WrapOutBind;
import com.x.organization.core.entity.Bind;

class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<Bind, WrapOutBind> outCopier = BeanCopyToolsBuilder.create(Bind.class,
			WrapOutBind.class, null, WrapOutBind.Excludes);

}

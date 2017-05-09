package com.x.portal.assemble.surface.jaxrs.portal;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.portal.assemble.surface.wrapout.WrapOutPortal;
import com.x.portal.core.entity.Portal;

abstract class ActionBase {

	static BeanCopyTools<Portal, WrapOutPortal> outCopier = BeanCopyToolsBuilder.create(Portal.class,
			WrapOutPortal.class, null, WrapOutPortal.Excludes);

}

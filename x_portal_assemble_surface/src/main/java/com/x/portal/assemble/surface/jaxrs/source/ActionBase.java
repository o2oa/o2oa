package com.x.portal.assemble.surface.jaxrs.source;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.portal.assemble.surface.wrapout.WrapOutSource;
import com.x.portal.core.entity.Source;

abstract class ActionBase {

	static BeanCopyTools<Source, WrapOutSource> outCopier = BeanCopyToolsBuilder.create(Source.class,
			WrapOutSource.class, null, WrapOutSource.Excludes);

}

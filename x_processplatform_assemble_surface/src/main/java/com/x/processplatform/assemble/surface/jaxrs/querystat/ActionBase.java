package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.QueryStat;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<QueryStat, WrapOutQueryStat> outCopier = BeanCopyToolsBuilder.create(QueryStat.class,
			WrapOutQueryStat.class, null, WrapOutQueryStat.Excludes);

}
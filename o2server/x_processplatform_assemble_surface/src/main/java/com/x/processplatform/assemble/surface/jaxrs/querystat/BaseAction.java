package com.x.processplatform.assemble.surface.jaxrs.querystat;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.QueryStat;

abstract class BaseAction extends StandardJaxrsAction {

	static WrapCopier<QueryStat, WrapOutQueryStat> outCopier = WrapCopierFactory.wo(QueryStat.class,
			WrapOutQueryStat.class, null, WrapOutQueryStat.Excludes);

}
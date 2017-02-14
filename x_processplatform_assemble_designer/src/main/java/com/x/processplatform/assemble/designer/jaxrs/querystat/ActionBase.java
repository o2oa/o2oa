package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.designer.wrapin.WrapInQueryStat;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryStat;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionBase extends StandardJaxrsAction {

	protected BeanCopyTools<QueryStat, WrapOutQueryStat> outCopier = BeanCopyToolsBuilder.create(QueryStat.class,
			WrapOutQueryStat.class, null, WrapOutQueryStat.Excludes);

	protected BeanCopyTools<WrapInQueryStat, QueryStat> inCopier = BeanCopyToolsBuilder.create(WrapInQueryStat.class,
			QueryStat.class, null, WrapInQueryStat.Excludes);

}

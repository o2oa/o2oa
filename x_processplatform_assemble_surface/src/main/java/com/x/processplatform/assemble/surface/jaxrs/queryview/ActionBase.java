package com.x.processplatform.assemble.surface.jaxrs.queryview;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutQueryView;
import com.x.processplatform.core.entity.element.QueryView;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<QueryView, WrapOutQueryView> outCopier = BeanCopyToolsBuilder.create(QueryView.class,
			WrapOutQueryView.class, null, WrapOutQueryView.Excludes);

}
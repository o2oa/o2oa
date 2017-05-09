package com.x.cms.assemble.control.jaxrs.queryview;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.element.QueryView;


public abstract class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<QueryView, WrapOutQueryView> outCopier = BeanCopyToolsBuilder.create(QueryView.class, WrapOutQueryView.class, null, WrapOutQueryView.Excludes);
	
}
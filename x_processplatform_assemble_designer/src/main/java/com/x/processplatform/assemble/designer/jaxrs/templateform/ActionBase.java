package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.entity.JpaObject;
import com.x.processplatform.assemble.designer.wrapin.WrapInTemplateForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutTemplateForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutTemplateFormSimple;
import com.x.processplatform.core.entity.element.TemplateForm;

abstract class ActionBase extends StandardJaxrsAction {

	/** 设计端的id都是由前台提供的这里要忽略拷贝id */
	private static String[] copyInExcludes = new String[] { JpaObject.DISTRIBUTEFACTOR, "updateTime", "createTime",
			"sequence" };

	static BeanCopyTools<TemplateForm, WrapOutTemplateForm> outCopier = BeanCopyToolsBuilder.create(TemplateForm.class,
			WrapOutTemplateForm.class, null, WrapOutTemplateForm.Excludes);

	static BeanCopyTools<TemplateForm, WrapOutTemplateFormSimple> simpleOutCopier = BeanCopyToolsBuilder
			.create(TemplateForm.class, WrapOutTemplateFormSimple.class, null, WrapOutTemplateFormSimple.Excludes);

	static BeanCopyTools<WrapInTemplateForm, TemplateForm> inCopier = BeanCopyToolsBuilder
			.create(WrapInTemplateForm.class, TemplateForm.class, null, copyInExcludes);

}

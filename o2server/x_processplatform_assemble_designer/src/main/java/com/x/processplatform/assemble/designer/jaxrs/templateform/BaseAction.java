package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.wrapout.WrapOutTemplateFormSimple;
import com.x.processplatform.core.entity.element.TemplateForm;

abstract class BaseAction extends StandardJaxrsAction {

//	/** 设计端的id都是由前台提供的这里要忽略拷贝id */
//	private static String[] copyInExcludes = new String[] { JpaObject.createTime_FIELDNAME,
//			JpaObject.updateTime_FIELDNAME, JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME };
//
//	static WrapCopier<TemplateForm, WrapOutTemplateForm> outCopier = WrapCopierFactory.create(TemplateForm.class,
//			WrapOutTemplateForm.class, null, WrapOutTemplateForm.Excludes);
//
	static WrapCopier<TemplateForm, WrapOutTemplateFormSimple> simpleOutCopier = WrapCopierFactory
			.wo(TemplateForm.class, WrapOutTemplateFormSimple.class, null, JpaObject.FieldsInvisible);
//
//	static WrapCopier<WrapInTemplateForm, TemplateForm> inCopier = WrapCopierFactory
//			.create(WrapInTemplateForm.class, TemplateForm.class, null, copyInExcludes);

}

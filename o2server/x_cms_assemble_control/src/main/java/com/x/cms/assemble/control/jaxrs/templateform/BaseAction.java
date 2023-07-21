package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.wrapout.WrapOutTemplateFormSimple;
import com.x.cms.core.entity.element.TemplateForm;


abstract class BaseAction extends StandardJaxrsAction {

	static WrapCopier<TemplateForm, WrapOutTemplateFormSimple> simpleOutCopier = WrapCopierFactory
			.wo(TemplateForm.class, WrapOutTemplateFormSimple.class, null, JpaObject.FieldsInvisible);

}

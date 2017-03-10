package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.designer.wrapin.WrapInForm;
import com.x.processplatform.assemble.designer.wrapin.WrapInFormField;
import com.x.processplatform.assemble.designer.wrapout.WrapOutForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutFormField;
import com.x.processplatform.assemble.designer.wrapout.WrapOutFormSimple;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;

class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<Form, WrapOutForm> outCopier = BeanCopyToolsBuilder.create(Form.class, WrapOutForm.class, null,
			WrapOutForm.Excludes);

	static BeanCopyTools<Form, WrapOutFormSimple> simpleOutCopier = BeanCopyToolsBuilder.create(Form.class,
			WrapOutFormSimple.class, null, WrapOutFormSimple.Excludes);

	static BeanCopyTools<WrapInForm, Form> inCopier = BeanCopyToolsBuilder.create(WrapInForm.class, Form.class, null,
			WrapInForm.Excludes);

	static BeanCopyTools<FormField, WrapOutFormField> formFieldOutCopier = BeanCopyToolsBuilder.create(FormField.class,
			WrapOutFormField.class, null, WrapOutFormField.Excludes);

	static BeanCopyTools<WrapInFormField, FormField> formFieldInCopier = BeanCopyToolsBuilder
			.create(WrapInFormField.class, FormField.class, null, WrapInFormField.Excludes);

}

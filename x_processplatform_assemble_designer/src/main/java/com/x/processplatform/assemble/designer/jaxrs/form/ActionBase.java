package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.designer.wrapin.WrapInForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutFormSimple;
import com.x.processplatform.core.entity.element.Form;

public class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<Form, WrapOutForm> outCopier = BeanCopyToolsBuilder.create(Form.class,
			WrapOutForm.class, null, WrapOutForm.Excludes);

	protected static BeanCopyTools<Form, WrapOutFormSimple> simpleOutCopier = BeanCopyToolsBuilder.create(Form.class,
			WrapOutFormSimple.class, null, WrapOutFormSimple.Excludes);

	protected static BeanCopyTools<WrapInForm, Form> inCopier = BeanCopyToolsBuilder.create(WrapInForm.class,
			Form.class, null, WrapInForm.Excludes);
}

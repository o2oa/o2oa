package com.x.organization.assemble.authentication.jaxrs.bind;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.authentication.wrapout.WrapOutBind;
import com.x.organization.core.entity.Bind;

class BaseAction extends StandardJaxrsAction {

	protected static WrapCopier<Bind, WrapOutBind> outCopier = WrapCopierFactory.wo(Bind.class,
			WrapOutBind.class, null, JpaObject.FieldsInvisible);

}

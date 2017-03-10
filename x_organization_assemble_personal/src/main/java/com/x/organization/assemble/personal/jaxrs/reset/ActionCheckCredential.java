package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;

class ActionCheckCredential extends ActionBase {

	ActionResult<WrapOutBoolean> execute(String credential) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(this.credentialExisted(emc, credential));
			result.setData(wrap);
			return result;
		}
	}

}

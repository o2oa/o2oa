package com.x.organization.assemble.authentication.jaxrs.authentication;


import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

class ActionCheckCredential extends ActionBase {
	
	private static Logger logger = LoggerFactory.getLogger(ActionCheckCredential.class);

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

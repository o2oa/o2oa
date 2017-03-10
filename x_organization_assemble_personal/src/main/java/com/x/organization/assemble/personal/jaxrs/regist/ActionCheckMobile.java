package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;

class ActionCheckMobile extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckMobile.class);

	ActionResult<WrapOutBoolean> execute(String mobile) throws Exception {

		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (!StringTools.isMobile(mobile)) {
				throw new InvalidMobileException(mobile);
			}
			if (this.mobileExisted(emc, mobile)) {
				throw new MobileExistedException(mobile);
			}
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new DisableCollectException();
			}
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

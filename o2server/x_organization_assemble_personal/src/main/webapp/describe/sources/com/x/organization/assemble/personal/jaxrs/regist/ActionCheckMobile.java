package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionCheckMobile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckMobile.class);

	ActionResult<WrapOutBoolean> execute(String mobile) throws Exception {

		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (!Config.person().isMobile(mobile)) {
				throw new ExceptionInvalidMobile(mobile);
			}
			if (this.mobileExisted(emc, mobile)) {
				throw new ExceptionMobileExist(mobile);
			}
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new ExceptionDisableCollect();
			}
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

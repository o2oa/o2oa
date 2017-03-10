package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.organization.assemble.personal.Business;

class ActionCode extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCode.class);

	ActionResult<WrapOutBoolean> execute(String mobile) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!StringUtils.equals(com.x.base.core.project.server.Person.REGISTER_TYPE_CODE,
					Config.person().getRegister())) {
				throw new DisableCodeException();
			}
			if (!StringTools.isMobile(mobile)) {
				throw new InvalidMobileException(mobile);
			}
			if (this.mobileExisted(emc, mobile)) {
				throw new MobileExistedException(mobile);
			}
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new DisableCollectException();
			}
			business.instrument().code().create(mobile);
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

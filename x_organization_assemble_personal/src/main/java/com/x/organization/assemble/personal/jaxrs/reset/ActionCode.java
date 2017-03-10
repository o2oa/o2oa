package com.x.organization.assemble.personal.jaxrs.reset;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionCode extends ActionBase {

	ActionResult<WrapOutBoolean> execute(String credential) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			WrapOutBoolean wrap = new WrapOutBoolean();
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new DisableCollectException();
			}
			String id = business.person().getWithCredential(credential);
			if (StringUtils.isEmpty(id)) {
				throw new PersonNotExistedException(credential);
			}

			Person o = emc.find(id, Person.class, ExceptionWhen.not_found);
			if (!StringTools.isMobile(o.getMobile())) {
				throw new InvalidMobileException();
			}
			business.instrument().code().create(o.getMobile());
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

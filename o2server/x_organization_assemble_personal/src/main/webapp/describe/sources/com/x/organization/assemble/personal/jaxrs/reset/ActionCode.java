package com.x.organization.assemble.personal.jaxrs.reset;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionCode extends BaseAction {

	ActionResult<WrapOutBoolean> execute(String credential) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutBoolean> result = new ActionResult<>();
			WrapOutBoolean wrap = new WrapOutBoolean();
			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(Config.collect().getEnable())) {
				throw new ExceptionDisableCollect();
			}
			Person person = business.person().getWithCredential(credential);
			if (null == person) {
				throw new ExceptionPersonNotExisted(credential);
			}

			person = emc.find(person.getId(), Person.class);
			if (!Config.person().isMobile(person.getMobile())) {
				throw new ExceptionInvalidMobile(person.getMobile());
			}
			business.instrument().code().create(person.getMobile());
			wrap.setValue(true);
			result.setData(wrap);
			return result;
		}
	}

}

package com.x.organization.assemble.control.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;

public class ActionGet extends ActionBase {

	protected WrapOutPerson execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		WrapOutPerson wrap = null;
		if (StringUtils.equalsIgnoreCase(Config.administrator().getId(), id)) {
			/* 如果是xadmin单独处理 */
			wrap = new WrapOutPerson();
			Config.administrator().copyTo(wrap, "password");
		} else {
			Person o = emc.find(id, Person.class, ExceptionWhen.not_found);
			wrap = outCopier.copy(o);
		}
		return wrap;
	}

}

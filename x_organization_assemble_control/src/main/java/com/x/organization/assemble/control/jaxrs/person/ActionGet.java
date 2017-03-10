package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;

class ActionGet extends ActionBase {

	protected ActionResult<WrapOutPerson> execute(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutPerson> result = new ActionResult<>();
			WrapOutPerson wrap = null;
			if (Config.token().isInitialManager(id)) {
				/* 如果是xadmin单独处理 */
				wrap = new WrapOutPerson();
				Config.token().initialManagerInstance().copyTo(wrap, "password");
			} else {
				Person o = emc.find(id, Person.class);
				if (null == o) {
					throw new PersonNotExistedException(id);
				}
				wrap = outCopier.copy(o);
				this.updateIcon(wrap);
			}
			result.setData(wrap);
			return result;
		}
	}

}

package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionList extends ActionBase {

	ActionResult<List<WrapOutApplication>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
			List<WrapOutApplication> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPerson(effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			wraps = outCopier.copy(emc.list(Application.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}

}
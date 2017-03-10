package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.core.entity.content.Work;

class ActionGet extends ActionBase {

	ActionResult<WrapOutWork> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new NotManagerException(effectivePerson.getName());
			}
			ActionResult<WrapOutWork> result = new ActionResult<>();
			Work work = emc.find(id, Work.class);
			if (null != work) {
				WrapOutWork wrap = workOutCopier.copy(work);
				result.setData(wrap);
			}
			return result;
		}
	}

}
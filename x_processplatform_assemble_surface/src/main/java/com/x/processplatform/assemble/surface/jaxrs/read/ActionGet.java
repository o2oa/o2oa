package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutRead;
import com.x.processplatform.core.entity.content.Read;

class ActionGet extends ActionBase {

	ActionResult<WrapOutRead> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<WrapOutRead> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ReadNotExistedException(id);
			}
			if (!business.read().allowProcessing(effectivePerson, read)) {
				throw new ReadAccessDeniedException(effectivePerson.getName(), id);
			}
			WrapOutRead wrap = readOutCopier.copy(read);
			result.setData(wrap);
			return result;
		}
	}
}

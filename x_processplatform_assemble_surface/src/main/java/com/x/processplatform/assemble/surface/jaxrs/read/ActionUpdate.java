package com.x.processplatform.assemble.surface.jaxrs.read;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInRead;
import com.x.processplatform.core.entity.content.Read;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInRead wrapIn = this.convertToWrapIn(jsonElement, WrapInRead.class);
			Business business = new Business(emc);
			Read read = emc.find(id, Read.class);
			if (null == read) {
				throw new ReadNotExistedException(id);
			}
			if (business.read().allowProcessing(effectivePerson, read)) {
				throw new ReadAccessDeniedException(effectivePerson.getName(), read.getId());
			}
			emc.beginTransaction(Read.class);
			readInCopier.copy(wrapIn, read);
			emc.check(read, CheckPersistType.all);
			emc.commit();
			WrapOutId wrap = new WrapOutId(id);
			result.setData(wrap);
			return result;
		}
	}

}

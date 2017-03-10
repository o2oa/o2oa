package com.x.processplatform.assemble.surface.jaxrs.data;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionGetWithWorkCompletedPath7 extends ActionBase {

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String id, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new WorkCompletedNotExistedException(id);
			}
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new WorkCompletedAccessDeniedException(effectivePerson.getName(), workCompleted.getTitle(), workCompleted.getId());
			}
			result.setData(this.getData(business, workCompleted.getJob(), path0, path1, path2, path3, path4, path5,
					path6, path7));
			return result;
		}
	}
}

package com.x.processplatform.assemble.surface.jaxrs.data;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionGetWithWorkCompletedPath7 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWithWorkCompletedPath7.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String id, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
						workCompleted.getTitle(), workCompleted.getId());
			}
			if (BooleanUtils.isTrue(workCompleted.getMerged())) {
				Data data = workCompleted.getData();
				Object o = data.find(new String[] { path0, path1, path2, path3, path4, path5, path6, path7 });
				result.setData(gson.toJsonTree(o));
			} else {
				result.setData(this.getData(business, workCompleted.getJob(), path0, path1, path2, path3, path4, path5,
						path6, path7));
			}
			return result;
		}
	}

}

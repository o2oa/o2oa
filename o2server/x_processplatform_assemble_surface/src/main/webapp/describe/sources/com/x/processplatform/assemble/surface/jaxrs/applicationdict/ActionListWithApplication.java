package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionListWithApplication extends BaseAction {

	ActionResult<List<WrapOutApplicationDict>> execute(String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutApplicationDict>> result = new ActionResult<>();
			List<WrapOutApplicationDict> wraps = new ArrayList<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			List<String> ids = business.applicationDict().listWithApplication(application);
			wraps = copier.copy(emc.list(ApplicationDict.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}

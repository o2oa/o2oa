package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.Application;

class ActionListWithApplication extends ActionBase {

	ActionResult<List<WrapOutApplicationDict>> execute(String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutApplicationDict>> result = new ActionResult<>();
			List<WrapOutApplicationDict> wraps = new ArrayList<>();
			Application application = business.application().pick(applicationFlag, ExceptionWhen.not_found);
			List<String> ids = business.applicationDict().listWithApplication(application);
			for (String id : ids) {
				wraps.add(copier.copy(business.applicationDict().pick(id)));
			}
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}
}

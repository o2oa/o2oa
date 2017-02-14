package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutFormSimple;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;

class ActionListWithApplication extends ActionBase {
	ActionResult<List<WrapOutFormSimple>> execute(EffectivePerson effectivePerson, String applicationId)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutFormSimple>> result = new ActionResult<>();
			List<WrapOutFormSimple> wraps = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.form().listWithApplication(applicationId);
			List<Form> os = emc.list(Form.class, ids);
			wraps = simpleOutCopier.copy(os);
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}

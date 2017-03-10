package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutProcess;
import com.x.processplatform.core.entity.element.Application;

class ActionListWithPersonWithApplication extends ActionBase {

	ActionResult<List<WrapOutProcess>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutProcess>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<WrapOutProcess> wraps = new ArrayList<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistedException(applicationFlag);
			}
			if (!business.application().allowRead(effectivePerson, application)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has insufficient permissions.");
			}
			List<String> identities = new ArrayList<>();
			for (WrapIdentity o : business.organization().identity().listWithPerson(effectivePerson.getName())) {
				identities.add(o.getName());
			}
			List<String> departments = new ArrayList<>();
			for (WrapDepartment o : business.organization().department().listWithPerson(effectivePerson.getName())) {
				departments.add(o.getName());
			}
			List<String> companies = new ArrayList<>();
			for (WrapCompany o : business.organization().company().listWithPerson(effectivePerson.getName())) {
				companies.add(o.getName());
			}
			List<String> ids = business.process().listStartableWithApplication(effectivePerson, identities, departments,
					companies, application);
			for (String id : ids) {
				wraps.add(processCopier.copy(business.process().pick(id)));
			}
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
			return result;
		}
	}

}

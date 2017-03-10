package com.x.processplatform.assemble.surface.jaxrs.application;

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
import com.x.organization.core.express.wrap.WrapRole;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplication;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutProcess;
import com.x.processplatform.core.entity.element.Application;

class ActionListWithPersonComplex extends ActionBase {

	ActionResult<List<WrapOutApplication>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
			List<WrapOutApplication> wraps = new ArrayList<>();
			Business business = new Business(emc);
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
			List<String> roles = new ArrayList<>();
			for (WrapRole o : business.organization().role().listWithPerson(effectivePerson.getName())) {
				companies.add(o.getName());
			}
			List<String> ids = business.application().listAvailable(effectivePerson, roles, identities, departments,
					companies);
			for (String id : ids) {
				Application o = business.application().pick(id);
				if (null != o) {
					WrapOutApplication wrap = applicationOutCopier.copy(o);
					wrap.setProcessList(
							this.listProcess(business, effectivePerson, identities, departments, companies, o));
					wraps.add(wrap);
				}
			}
			result.setData(wraps);
			return result;
		}
	}

	private List<WrapOutProcess> listProcess(Business business, EffectivePerson effectivePerson,
			List<String> identities, List<String> departments, List<String> companies, Application application)
			throws Exception {
		List<String> ids = business.process().listStartableWithApplication(effectivePerson, identities, departments,
				companies, application);
		List<WrapOutProcess> wraps = new ArrayList<>();
		for (String id : ids) {
			WrapOutProcess o = processOutCopier.copy(business.process().pick(id));
			wraps.add(o);
		}
		SortTools.asc(wraps, "name");
		return wraps;
	}
}

package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
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
			List<String> identities = business.organization().identity().listNameWithPerson(effectivePerson.getName());
			/** 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用 */
			List<String> departments = business.organization().department()
					.listNameWithPersonSupNested(effectivePerson.getName());
			/** 去除部门以及上级公司,如果设置了一级公司可用,那么一级公司下属的二级公司也可用 */
			List<String> companies = business.organization().company()
					.listNameWithPersonSupNested(effectivePerson.getName());
			List<String> roles = business.organization().role().listNameWithPerson(effectivePerson.getName());
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

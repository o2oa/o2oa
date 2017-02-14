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
import com.x.processplatform.core.entity.element.Application;

class ActionListWithPerson extends ActionBase {

	/*
	 * 1.身份在可使用列表中 2.部门在可使用部门中 3.公司在可使用公司中 4.没有限定身份,部门或者公司 5.个人在应用管理员中
	 * 6.个人有Manage权限或者ProcessPlatformManager身份
	 */
	ActionResult<List<WrapOutApplication>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
			List<WrapOutApplication> wraps = new ArrayList<>();
			List<String> identities = new ArrayList<>();
			List<String> roles = new ArrayList<>();
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
			for (WrapRole o : business.organization().role().listWithPerson(effectivePerson.getName())) {
				roles.add(o.getName());
			}
			List<String> ids = business.application().listAvailable(effectivePerson, roles, identities, departments,
					companies);
			for (String id : ids) {
				Application o = business.application().pick(id);
				wraps.add(applicationOutCopier.copy(o));
			}
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}
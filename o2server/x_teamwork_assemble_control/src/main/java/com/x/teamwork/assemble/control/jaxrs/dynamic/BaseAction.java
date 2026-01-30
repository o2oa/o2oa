package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.service.*;

public class BaseAction extends StandardJaxrsAction {

	protected DynamicPersistService dynamicPersistService = new DynamicPersistService();

	protected DynamicQueryService dynamicQueryService = new DynamicQueryService();

	protected ChatQueryService chatQueryService = new ChatQueryService();

	protected TaskQueryService taskQueryService = new TaskQueryService();

	protected ProjectQueryService projectQueryService = new ProjectQueryService();

	protected boolean isReader(String id, EffectivePerson effectivePerson, boolean isProject) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.isManager(effectivePerson) || business.permissionFactory().isReader(id, effectivePerson.getDistinguishedName(), isProject);
		}
	}
}

package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/** 添加管理员和流程管理员删除的权限 */
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.ProcessPlatformManager))) {
				throw new ExceptionInsufficientPermission(effectivePerson.getDistinguishedName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			TemplateForm template = emc.find(id, TemplateForm.class);
			if (null == template) {
				throw new ExceptionTemplateFormNotExist(id);
			}
			emc.beginTransaction(TemplateForm.class);
			emc.remove(template, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(TemplateForm.class);
			Wo wo = new Wo();
			wo.setId(template.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}
}

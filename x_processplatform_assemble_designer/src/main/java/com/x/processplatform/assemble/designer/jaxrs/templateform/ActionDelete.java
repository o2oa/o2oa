package com.x.processplatform.assemble.designer.jaxrs.templateform;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.role.RoleDefinition;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.TemplateForm;

class ActionDelete extends ActionBase {
	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/** 添加管理员和流程管理员删除的权限 */
			if (effectivePerson.isNotManager() && (!business.organization().role().hasAny(effectivePerson.getName(),
					RoleDefinition.ProcessPlatformManager))) {
				throw new InsufficientPermissionException(effectivePerson.getName());
			}
			ActionResult<WrapOutId> result = new ActionResult<>();
			TemplateForm template = emc.find(id, TemplateForm.class, ExceptionWhen.not_found);
			emc.beginTransaction(TemplateForm.class);
			emc.remove(template, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(TemplateForm.class);
			WrapOutId wrap = new WrapOutId(template.getId());
			result.setData(wrap);
			return result;
		}
	}
}

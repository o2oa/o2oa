package com.x.message.assemble.communicate.jaxrs.mass;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Mass;

class ActionEnableType extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.MessageManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();

			Wo wo = new Wo();

			if (Config.qiyeweixin().getEnable()) {
				wo.setValue(Mass.TYPE_QIYEWEIXIN);
			} else if (Config.dingding().getEnable()) {
				wo.setValue(Mass.TYPE_DINGDING);
			} else if (Config.zhengwuDingding().getEnable()) {
				wo.setValue(Mass.TYPE_ZHENGWUDINGDING);
			} else {
				wo.setValue("");
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}

}

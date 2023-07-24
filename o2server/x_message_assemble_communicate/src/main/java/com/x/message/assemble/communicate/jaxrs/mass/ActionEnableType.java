package com.x.message.assemble.communicate.jaxrs.mass;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Mass;

class ActionEnableType extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEnableType.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.MessageManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();

			Wo wo = new Wo();

			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())) {
				wo.setValue(Mass.TYPE_QIYEWEIXIN);
			} else if (BooleanUtils.isTrue(Config.dingding().getEnable())) {
				wo.setValue(Mass.TYPE_DINGDING);
			} else if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())) {
				wo.setValue(Mass.TYPE_ZHENGWUDINGDING);
			} else if (BooleanUtils.isTrue(Config.weLink().getEnable())
					&& BooleanUtils.isTrue(Config.weLink().getMessageEnable())) {
				wo.setValue(Mass.TYPE_WELINK);
			} else {
				wo.setValue("");
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = -1681442801874856071L;

	}

}

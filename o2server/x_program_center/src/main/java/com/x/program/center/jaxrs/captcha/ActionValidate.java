package com.x.program.center.jaxrs.captcha;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.core.entity.Captcha;

class ActionValidate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String answer) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Captcha captcha = emc.find(id, Captcha.class);
			if (null == captcha) {
				wo.setValue(false);
			} else {
				boolean match = this.check(captcha.getAnswer(), answer);
				if (match) {
					emc.beginTransaction(Captcha.class);
					emc.remove(captcha);
					emc.commit();
				}
				wo.setValue(match);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7607770513162254586L;

	}

	private Boolean check(String value, String answer) {
		return StringUtils.equalsIgnoreCase(value, answer);
	}
}
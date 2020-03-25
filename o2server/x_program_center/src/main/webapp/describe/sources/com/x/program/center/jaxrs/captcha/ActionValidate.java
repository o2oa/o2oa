package com.x.program.center.jaxrs.captcha;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.core.entity.Captcha;

class ActionValidate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String answer) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new Exception("insufficient permissions.");
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Captcha captcha = emc.find(id, Captcha.class);
			if (null == captcha) {
				wo.setValue(false);
			} else {
				Boolean match = this.check(captcha.getAnswer(), answer);
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

	}

	private Boolean check(String value, String answer) {
		if (StringUtils.equalsIgnoreCase(value, answer)) {
			return true;
		}
		if (StringUtils.length(value) == StringUtils.length(answer)) {
			int match = 0;
			for (int i = 0; i < value.length(); i++) {
				if (value.charAt(i) == answer.charAt(i)) {
					match++;
				}
			}
			if (match >= (value.length() - 1)) {
				return true;
			}
		}
		return false;
	}
}
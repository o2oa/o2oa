package com.x.program.center.jaxrs.test;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Captcha;
import com.x.program.center.core.entity.Code;

class ActionTest4 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest4.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(Captcha.class);
			emc.beginTransaction(Code.class);
			Captcha c1 = new Captcha();
			c1.setAnswer("123");
			emc.persist(c1);
			Captcha c2 = new Captcha();
			emc.persist(c2);

			Code code2 = new Code();
			code2.setAnswer("111111");
			emc.persist(code2);
			emc.commit();
		}
		Wo wo = new Wo();
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 3854135292216248503L;

		@FieldDescribe("执行脚本.")
		private String text;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

}
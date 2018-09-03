package o2.collect.assemble.jaxrs.code;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;

class ActionValidate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionValidate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String mobile, String answer) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (StringUtils.isEmpty(mobile)) {
				throw new ExceptionMobileEmpty();
			}
			if (StringUtils.isEmpty(answer)) {
				throw new ExceptionAnswerEmpty();
			}
			boolean value = business.validateCode(mobile, answer, null, false);
			Wo wo = new Wo();
			wo.setValue(value);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}

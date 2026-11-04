package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.StringTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCheckName extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (com.x.base.core.project.config.Person.REGISTER_TYPE_DISABLE.equals(
					Config.person().getRegister())) {
				throw new ExceptionDisableRegist();
			}
			if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name))) {
				throw new ExceptionInvalidName(name);
			}
			if (this.nameExisted(emc, name)) {
				throw new ExceptionNameExist(name);
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}

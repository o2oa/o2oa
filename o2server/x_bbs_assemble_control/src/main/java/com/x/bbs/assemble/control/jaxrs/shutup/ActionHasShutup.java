package com.x.bbs.assemble.control.jaxrs.shutup;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.userinfo.BaseAction;
import com.x.bbs.entity.BBSShutup;

/**
 * @author sword
 */
public class ActionHasShutup extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		wrap.setValue(false);
		result.setData(wrap);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Long count = emc.countEqual(BBSShutup.class, BBSShutup.person_FIELDNAME, effectivePerson.getDistinguishedName());
			if(count > 0){
				wrap.setValue(true);
			}
		}
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}

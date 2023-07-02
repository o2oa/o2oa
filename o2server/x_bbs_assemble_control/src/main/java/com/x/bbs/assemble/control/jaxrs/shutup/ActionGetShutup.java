package com.x.bbs.assemble.control.jaxrs.shutup;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.bbs.assemble.control.jaxrs.userinfo.BaseAction;
import com.x.bbs.entity.BBSShutup;

/**
 * @author sword
 */
public class ActionGetShutup extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			BBSShutup shutup = emc.firstEqual(BBSShutup.class, BBSShutup.person_FIELDNAME, effectivePerson.getDistinguishedName());
			Wo wrap = Wo.copier.copy(shutup);
			result.setData(wrap);
		}
		return result;
	}

	public static class Wo extends BBSShutup {

		private static final long serialVersionUID = 1326540475648633343L;

		static WrapCopier<BBSShutup, Wo> copier = WrapCopierFactory.wo(BBSShutup.class, Wo.class,
				JpaObject.singularAttributeField(BBSShutup.class, true, true), null);
	}
}

package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapin.element.WrapInApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String flag, WrapInApplicationDict wrapIn)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			ApplicationDict dict = emc.flag(flag, ApplicationDict.class, ExceptionWhen.not_found, true,
					ApplicationDict.FLAGS);
			Application application = business.application().pick(dict.getApplication(), ExceptionWhen.not_found);
			if (!business.application().allowControl(effectivePerson, application)) {
				throw new Exception("person{name;" + effectivePerson.getName() + "} update dict denied.");
			}
			emc.beginTransaction(ApplicationDict.class);
			wrapIn.copyTo(dict, JpaObject.ID, JpaObject.DISTRIBUTEFACTOR, "application");
			emc.check(dict, CheckPersistType.all);
			this.update(business, dict, wrapIn.getData());
			emc.commit();
			/* 这个Action是更新ApplicationDict需要刷新缓存 */
			ApplicationCache.notify(ApplicationDict.class);
			WrapOutId wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
			return result;
		}
	}

}

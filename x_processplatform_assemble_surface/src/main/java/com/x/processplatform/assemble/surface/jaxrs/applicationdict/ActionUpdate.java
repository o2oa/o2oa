package com.x.processplatform.assemble.surface.jaxrs.applicationdict;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapin.element.WrapInApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String applicationDictFlag, String applicationFlag,
			WrapInApplicationDict wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ApplicationNotExistException(applicationFlag);
			}
			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(),
					applicationDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ApplicationDictNotExistException(applicationFlag);
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			if (!business.application().allowControl(effectivePerson, application)) {
				throw new AccessDeniedException(effectivePerson.getName());
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

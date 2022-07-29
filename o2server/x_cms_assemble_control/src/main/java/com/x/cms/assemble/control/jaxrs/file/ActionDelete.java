package com.x.cms.assemble.control.jaxrs.file;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.File;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			File file = emc.flag(flag, File.class);
			if (null == file) {
				throw new ExceptionEntityNotExist(flag, File.class);
			}
			AppInfo application = emc.find(file.getAppId(), AppInfo.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(file.getAppId(), AppInfo.class);
			}
			if (!business.isAppInfoManager(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(File.class);
			emc.remove(file, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(File.class);
			Wo wo = new Wo();
			wo.setId(file.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

}

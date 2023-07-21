package com.x.cms.assemble.control.jaxrs.appdict;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionUpdate extends BaseAction {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String appDictFlag, String appInfoFlag, WrapInAppDict wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			AppInfo appInfo = business.getAppInfoFactory().pick(appInfoFlag);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appInfoFlag);
			}
			String id = business.getAppDictFactory().getWithAppInfoWithUniqueName(appInfo.getId(),
					appDictFlag);
			if (StringUtils.isEmpty(id)) {
				throw new ExceptionAppDictNotExist(appInfoFlag);
			}
			AppDict dict = emc.find(id, AppDict.class);
			emc.beginTransaction(AppDict.class);
			wrapIn.copyTo(dict, JpaObject.id_FIELDNAME, JpaObject.distributeFactor_FIELDNAME, "appInfo");
			emc.check(dict, CheckPersistType.all);
			this.update(business, dict, wrapIn.getData());
			emc.commit();
			/* 这个Action是更新AppDict需要刷新缓存 */
			CacheManager.notify(AppDict.class);
			WrapOutId wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
			return result;
		}
	}

}

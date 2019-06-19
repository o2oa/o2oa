package com.x.cms.assemble.control.jaxrs.appdictdesign;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			AppDict dict = emc.find(id, AppDict.class);
			if (null == dict) {
				throw new ExceptionAppDictNotExisted(id);
			}
			AppInfo appInfo = emc.find(dict.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(dict.getAppId());
			}
			if (!business.editable(effectivePerson, appInfo)) {
				throw new ExceptionAppInfoAccessDenied(effectivePerson.getDistinguishedName(),
						appInfo.getAppName(), appInfo.getId());
			}
			List<String> ids = business.getAppDictItemFactory().listWithAppDict(id);
			this.delete_batch(emc, ids);
			emc.beginTransaction(AppDict.class);
			emc.remove(dict, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(AppDict.class);
			Wo wo = new Wo();
			wo.setId(dict.getId());
			result.setData(wo);
			return result;
		}
	}

	private void delete_batch(EntityManagerContainer emc, List<String> ids) throws Exception {
		for (int i = 0; i < ids.size(); i++) {
			if (i % 100 == 0) {
				emc.beginTransaction(AppDictItem.class);
				//emc.beginTransaction(AppDictLobItem.class);
			}
			AppDictItem o = emc.find(ids.get(i), AppDictItem.class);
			if (null != o) {
				emc.remove(o);
			}
			if ((i % 100 == 99) || i == (ids.size() - 1)) {
				emc.commit();
			}
		}
	}

	public static class Wo extends WoId {
	}

}
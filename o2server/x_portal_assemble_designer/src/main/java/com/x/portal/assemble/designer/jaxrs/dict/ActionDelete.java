package com.x.portal.assemble.designer.jaxrs.dict;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

import java.util.List;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			if (null == dict) {
				throw new ExceptionEntityNotExist(id);
			}
			Portal application = emc.find(dict.getApplication(), Portal.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(dict.getApplication(), Portal.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> ids = business.applicationDictItem().listWithApplicationDict(id);
			this.delete_batch(emc, ids);
			emc.beginTransaction(ApplicationDict.class);
			emc.remove(dict, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(ApplicationDict.class);
			Wo wo = new Wo();
			wo.setId(dict.getId());
			result.setData(wo);
			return result;
		}
	}

	private void delete_batch(EntityManagerContainer emc, List<String> ids) throws Exception {
		for (int i = 0; i < ids.size(); i++) {
			if (i % 100 == 0) {
				emc.beginTransaction(ApplicationDictItem.class);
			}
			ApplicationDictItem o = emc.find(ids.get(i), ApplicationDictItem.class);
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

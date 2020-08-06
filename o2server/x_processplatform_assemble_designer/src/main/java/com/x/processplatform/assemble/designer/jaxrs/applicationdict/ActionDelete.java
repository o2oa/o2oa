package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			if (null == dict) {
				throw new ApplicationDictNotExistedException(id);
			}
			Application application = emc.find(dict.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(dict.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
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
				// emc.beginTransaction(ApplicationDictLobItem.class);
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
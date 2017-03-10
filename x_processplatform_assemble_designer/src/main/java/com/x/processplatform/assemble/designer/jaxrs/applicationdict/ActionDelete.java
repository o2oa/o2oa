package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictLobItem;

class ActionDelete extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			Business business = new Business(emc);
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			if (null == dict) {
				throw new ApplicationDictNotExistedException(id);
			}
			Application application = emc.find(dict.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(dict.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			List<String> ids = business.applicationDictItem().listWithApplicationDict(id);
			this.delete_batch(emc, ids);
			emc.beginTransaction(ApplicationDict.class);
			emc.remove(dict, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(ApplicationDict.class);
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
			return result;
		}
	}

	private void delete_batch(EntityManagerContainer emc, List<String> ids) throws Exception {
		for (int i = 0; i < ids.size(); i++) {
			if (i % 100 == 0) {
				emc.beginTransaction(ApplicationDictItem.class);
				emc.beginTransaction(ApplicationDictLobItem.class);
			}
			ApplicationDictItem o = emc.find(ids.get(i), ApplicationDictItem.class);
			if (null != o) {
				if (o.isLobItem()) {
					/** 删除关联的lob */
					ApplicationDictLobItem lob = emc.find(o.getLobItem(), ApplicationDictLobItem.class);
					if (null != lob) {
						emc.remove(lob);
					}
				}
				emc.remove(o);
			}
			if ((i % 100 == 99) || i == (ids.size() - 1)) {
				emc.commit();
			}
		}
	}

}
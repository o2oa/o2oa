package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictLobItem;

class ActionUpdate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			WrapInApplicationDict wrapIn = this.convertToWrapIn(jsonElement, WrapInApplicationDict.class);
			Business business = new Business(emc);
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			if (null == dict) {
				throw new ApplicationDictNotExistedException(id);
			}
			Application application = emc.find(wrapIn.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(wrapIn.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			emc.beginTransaction(ApplicationDictLobItem.class);
			inCopier.copy(wrapIn, dict);
			dict.setApplication(application.getId());
			emc.check(dict, CheckPersistType.all);
			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> exists = business.applicationDictItem().listEntityWithApplicationDict(id);
			List<ApplicationDictItem> currents = converter.disassemble(wrapIn.getData());
			List<ApplicationDictItem> removes = converter.subtract(exists, currents);
			List<ApplicationDictItem> adds = converter.subtract(currents, exists);
			for (ApplicationDictItem o : removes) {
				if (o.isLobItem()) {
					ApplicationDictLobItem lob = emc.find(o.getLobItem(), ApplicationDictLobItem.class);
					emc.remove(lob);
				}
				emc.remove(o);
			}
			for (ApplicationDictItem o : adds) {
				o.setApplicationDict(dict.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(dict.getDistributeFactor());
				o.setApplication(application.getId());
				if (o.isLobItem()) {
					/** 同步创建lob */
					ApplicationDictLobItem lob = new ApplicationDictLobItem();
					lob.setDistributeFactor(o.getDistributeFactor());
					lob.setData(o.getStringLobValue());
					o.setLobItem(lob.getId());
					emc.persist(lob);
				}
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(ApplicationDict.class);
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
			return result;
		}
	}

}
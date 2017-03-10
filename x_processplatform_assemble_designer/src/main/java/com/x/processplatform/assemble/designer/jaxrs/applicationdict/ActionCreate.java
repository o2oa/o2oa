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

class ActionCreate extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			WrapInApplicationDict wrapIn = this.convertToWrapIn(jsonElement, WrapInApplicationDict.class);
			Business business = new Business(emc);
			Application application = emc.find(wrapIn.getApplication(), Application.class);
			if (null == application) {
				throw new ApplicationDictNotExistedException(wrapIn.getApplication());
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			emc.beginTransaction(ApplicationDictLobItem.class);
			ApplicationDict applicationDict = new ApplicationDict();
			inCopier.copy(wrapIn, applicationDict);
			applicationDict.setApplication(application.getId());
			emc.persist(applicationDict, CheckPersistType.all);
			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(wrapIn.getData());
			for (ApplicationDictItem o : list) {
				o.setApplicationDict(applicationDict.getId());
				o.setDistributeFactor(applicationDict.getDistributeFactor());
				o.setApplication(application.getId());
				if (o.isLobItem()) {
					/** 保存lob */
					ApplicationDictLobItem lob = new ApplicationDictLobItem();
					lob.setData(o.getStringLobValue());
					lob.setDistributeFactor(o.getDistributeFactor());
					o.setLobItem(lob.getId());
					emc.persist(lob);
				}
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(ApplicationDict.class);
			wrap = new WrapOutId(applicationDict.getId());
			result.setData(wrap);
			return result;
		}
	}

}
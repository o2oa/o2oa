package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictLobItem;

class ActionGet extends ActionBase {

	ActionResult<WrapOutApplicationDict> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutApplicationDict> result = new ActionResult<>();
			WrapOutApplicationDict wrap = new WrapOutApplicationDict();
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
			wrap = outCopier.copy(dict);
			List<ApplicationDictItem> items = business.applicationDictItem().listEntityWithApplicationDict(id);
			for (ApplicationDictItem o : items) {
				if (o.isLobItem()) {
					/** 载入关联的lob */
					ApplicationDictLobItem lob = emc.find(o.getLobItem(), ApplicationDictLobItem.class);
					if (null != lob) {
						o.setStringLobValue(lob.getData());
					}
				}
			}
			/* 由于需要排序重新生成可排序List */
			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
			JsonElement json = converter.assemble(items);
			wrap.setData(json);
			result.setData(wrap);
			return result;
		}
	}

}
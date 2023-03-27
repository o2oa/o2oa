package com.x.portal.assemble.designer.jaxrs.dict;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

import java.util.List;

class ActionGet extends BaseAction {

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
			Wo wo = Wo.copier.copy(dict);
			List<ApplicationDictItem> items = business.applicationDictItem().listWithApplicationDictObject(id);
			/* 由于需要排序重新生成可排序List */
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			JsonElement json = converter.assemble(items);
			wo.setData(json);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ApplicationDict {

		private static final long serialVersionUID = -939143099553857829L;

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private JsonElement data;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

	}

}

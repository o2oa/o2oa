package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;

class ActionGet extends BaseAction {

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
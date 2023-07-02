package com.x.cms.assemble.control.jaxrs.appdictdesign;

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
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

class ActionGet extends BaseAction {

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
			Wo wo = Wo.copier.copy(dict);
			List<AppDictItem> items = business.getAppDictItemFactory().listObjectWithAppDict(id);
			/* 由于需要排序重新生成可排序List */
			DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
			JsonElement json = converter.assemble(items);
			wo.setData(json);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends AppDict {

		private static final long serialVersionUID = -939143099553857829L;

		static WrapCopier<AppDict, Wo> copier = WrapCopierFactory.wo(AppDict.class, Wo.class, null,
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
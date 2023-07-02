package com.x.cms.assemble.control.jaxrs.appdictdesign;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			AppInfo appInfo = emc.find(wi.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionAppDictNotExisted(wi.getAppId());
			}
			Business business = new Business(emc);
			if (!business.isAppInfoManager(effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			//emc.beginTransaction(AppDictLobItem.class);
			AppDict appInfoDict = new AppDict();
			Wi.copier.copy(wi, appInfoDict);
			appInfoDict.setAppId(appInfo.getId());
			emc.persist(appInfoDict, CheckPersistType.all);
			DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
			List<AppDictItem> list = converter.disassemble(wi.getData());
			for (AppDictItem o : list) {
				o.setBundle(appInfoDict.getId());
				o.setDistributeFactor(appInfoDict.getDistributeFactor());
				o.setAppId(appInfo.getId());
				o.setItemCategory(ItemCategory.pp_dict);
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(AppDict.class);
			Wo wo = new Wo();
			wo.setId(appInfoDict.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends AppDict {

		private static final long serialVersionUID = 7020926328082641485L;

		static WrapCopier<Wi, AppDict> copier = WrapCopierFactory.wi(Wi.class, AppDict.class, null,
				Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

		private JsonElement data;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

	}

}

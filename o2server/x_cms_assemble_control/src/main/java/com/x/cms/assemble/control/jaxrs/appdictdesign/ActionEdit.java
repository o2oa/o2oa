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
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			AppDict dict = emc.find(id, AppDict.class);
			if (null == dict) {
				throw new ExceptionAppDictNotExisted(id);
			}
			AppInfo appInfo = emc.find(wi.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(wi.getAppId());
			}
			if (!business.editable(effectivePerson, appInfo)) {
				throw new ExceptionAppInfoAccessDenied(effectivePerson.getDistinguishedName(),
						appInfo.getAppName(), appInfo.getId());
			}
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			// emc.beginTransaction(AppDictLobItem.class);
			Wi.copier.copy(wi, dict);
			dict.setAppId(appInfo.getId());
			emc.check(dict, CheckPersistType.all);
			DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
			List<AppDictItem> exists = business.getAppDictItemFactory().listObjectWithAppDict(id);
			List<AppDictItem> currents = converter.disassemble(wi.getData());
			List<AppDictItem> removes = converter.subtract(exists, currents);
			List<AppDictItem> adds = converter.subtract(currents, exists);
			for (AppDictItem o : removes) {
				emc.remove(o);
			}
			for (AppDictItem o : adds) {
				o.setItemCategory(ItemCategory.pp_dict);
				o.setBundle(dict.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(dict.getDistributeFactor());
				o.setAppId(appInfo.getId());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(AppDict.class);
			Wo wo = new Wo();
			wo.setId(dict.getId());
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
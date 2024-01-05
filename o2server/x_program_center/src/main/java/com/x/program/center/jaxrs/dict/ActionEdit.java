package com.x.program.center.jaxrs.dict;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.program.center.Business;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ApplicationDict dict = emc.find(id, ApplicationDict.class);
			if (null == dict) {
				throw new ExceptionEntityNotExist(id);
			}

			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			Wi.copier.copy(wi, dict);
			dict.setApplication(ApplicationDict.PROJECT_SERVICE);
			dict.setProject(ApplicationDict.PROJECT_SERVICE);
			emc.check(dict, CheckPersistType.all);
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> exists = business.applicationDictItem().listWithApplicationDictObject(id);
			List<ApplicationDictItem> currents = converter.disassemble(wi.getData());
			List<ApplicationDictItem> removes = converter.subtract(exists, currents);
			List<ApplicationDictItem> adds = converter.subtract(currents, exists);
			for (ApplicationDictItem o : removes) {
				emc.remove(o);
			}
			for (ApplicationDictItem o : adds) {
				o.setItemCategory(ItemCategory.service_dict);
				o.setBundle(dict.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(dict.getDistributeFactor());
				o.setApplication(ApplicationDict.PROJECT_SERVICE);
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(ApplicationDict.class);
			Wo wo = new Wo();
			wo.setId(dict.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends ApplicationDict {

		private static final long serialVersionUID = -903147978817129105L;
		static WrapCopier<Wi, ApplicationDict> copier = WrapCopierFactory.wi(Wi.class, ApplicationDict.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, ApplicationDict.project_FIELDNAME, ApplicationDict.application_FIELDNAME));

		@FieldDescribe("字典数据(json格式).")
		private JsonElement data;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

	}

}

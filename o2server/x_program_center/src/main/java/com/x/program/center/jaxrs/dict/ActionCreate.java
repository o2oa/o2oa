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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.program.center.Business;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			ApplicationDict applicationDict = new ApplicationDict();
			Wi.copier.copy(wi, applicationDict);
			applicationDict.setApplication(ApplicationDict.PROJECT_SERVICE);
			applicationDict.setProject(ApplicationDict.PROJECT_SERVICE);
			emc.persist(applicationDict, CheckPersistType.all);
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(wi.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(applicationDict.getId());
				o.setDistributeFactor(applicationDict.getDistributeFactor());
				o.setApplication(ApplicationDict.PROJECT_SERVICE);
				o.setItemCategory(ItemCategory.service_dict);
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(ApplicationDict.class);
			Wo wo = new Wo();
			wo.setId(applicationDict.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2714867252705825023L;
	}

	public static class Wi extends ApplicationDict {

		private static final long serialVersionUID = -2234895947066232221L;

		static WrapCopier<Wi, ApplicationDict> copier = WrapCopierFactory.wi(Wi.class, ApplicationDict.class, null,
				ListTools.toList(JpaObject.FieldsUnmodifyExcludeId, ApplicationDict.project_FIELDNAME, ApplicationDict.application_FIELDNAME));

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

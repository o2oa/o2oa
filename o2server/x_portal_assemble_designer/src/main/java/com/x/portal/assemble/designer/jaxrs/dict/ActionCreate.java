package com.x.portal.assemble.designer.jaxrs.dict;

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
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;

import java.util.Arrays;
import java.util.List;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Portal application = emc.find(wi.getApplication(), Portal.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(wi.getApplication(), Portal.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			ApplicationDict applicationDict = new ApplicationDict();
			Wi.copier.copy(wi, applicationDict);
			applicationDict.setApplication(application.getId());
			applicationDict.setProject(ApplicationDict.PROJECT_PORTAL);
			emc.persist(applicationDict, CheckPersistType.all);
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(wi.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(applicationDict.getId());
				o.setDistributeFactor(applicationDict.getDistributeFactor());
				o.setApplication(application.getId());
				o.setItemCategory(ItemCategory.portal_dict);
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

		private static final long serialVersionUID = 7020926328082641485L;

		static WrapCopier<Wi, ApplicationDict> copier = WrapCopierFactory.wi(Wi.class, ApplicationDict.class, null,
				ListTools.toList(JpaObject.FieldsUnmodifyExcludeId, ApplicationDict.project_FIELDNAME));

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

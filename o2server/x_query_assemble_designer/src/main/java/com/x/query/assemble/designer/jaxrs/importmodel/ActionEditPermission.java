package com.x.query.assemble.designer.jaxrs.importmodel;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;

class ActionEditPermission extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ImportModel model = emc.find(id, ImportModel.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(id, ImportModel.class);
			}
			Query query = emc.find(model.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(model.getQuery(), Query.class);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query.getName());
			}
			emc.beginTransaction(ImportModel.class);
			Wi.copier.copy(wi, model);
			emc.check(model, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(ImportModel.class);
			Wo wo = new Wo();
			wo.setId(model.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends ImportModel {

		private static final long serialVersionUID = 4822000127324829559L;

		static WrapCopier<Wi, ImportModel> copier = WrapCopierFactory.wi(Wi.class, ImportModel.class,
				ListTools.toList(ImportModel.availableIdentityList_FIELDNAME, ImportModel.availableUnitList_FIELDNAME,
						ImportModel.availableGroupList_FIELDNAME), null);
	}
}

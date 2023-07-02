package com.x.query.assemble.designer.jaxrs.importmodel;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionAliasExist;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionNameExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;

class ActionCreate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Query query = emc.find(wi.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(wi.getQuery(), Query.class);
			}
			Business business = new Business(emc);
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query.getName());
			}
			emc.beginTransaction(ImportModel.class);
			ImportModel model = Wi.copier.copy(wi);
			switch (StringUtils.trimToEmpty(wi.getType())) {
			case ImportModel.TYPE_CMS:
				break;
			case ImportModel.TYPE_PROCESSPLATFORM:
				break;
			case ImportModel.TYPE_DYNAMIC_TABLE:
				break;
			default:
				throw new ExceptionTypeValue(wi.getType());
			}
			if (StringUtils.isNotEmpty(model.getName()) && (!this.idleName(business, model))) {
				throw new ExceptionNameExist(model.getName());
			}
			if (StringUtils.isNotEmpty(model.getAlias()) && (!this.idleAlias(business, model))) {
				throw new ExceptionAliasExist(model.getAlias());
			}
			model.setQuery(query.getId());
			emc.persist(model, CheckPersistType.all);
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

		private static final long serialVersionUID = -2552202172712945079L;

		static WrapCopier<Wi, ImportModel> copier = WrapCopierFactory.wi(Wi.class, ImportModel.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

	}

}

package com.x.query.assemble.surface.jaxrs.importmodel;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;

class ActionGetWithQuery extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String queryFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Query query = business.pick(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag, Query.class);
			}
			CacheKey cacheKey = new CacheKey(this.getClass(), flag, queryFlag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			ImportModel model = null;
			if (optional.isPresent()) {
				model = (ImportModel) optional.get();
			} else {
				String id = business.importModel().getWithQuery(flag, query);
				model = business.pick(id, ImportModel.class);
				if (null != model) {
					business.entityManagerContainer().get(ImportModel.class).detach(model);
					CacheManager.put(business.cache(), cacheKey, model);
				}
			}
			if (null == model) {
				throw new ExceptionEntityNotExist(flag, ImportModel.class);
			}
			if (!business.readable(effectivePerson, model)) {
				throw new ExceptionAccessDenied(effectivePerson, model);
			}
			Wo wo = Wo.copier.copy(model);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ImportModel {

		private static final long serialVersionUID = -314329348134833001L;

		static WrapCopier<ImportModel, Wo> copier = WrapCopierFactory.wo(ImportModel.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}

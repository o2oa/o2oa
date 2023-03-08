package com.x.query.assemble.surface.jaxrs.view;

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
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;

import java.util.Optional;

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
			View view = null;
			if (optional.isPresent()) {
				view = (View) optional.get();
			} else {
				String id = business.view().getWithQuery(flag, query);
				view = business.pick(id, View.class);
				if (null != view) {
					business.entityManagerContainer().get(View.class).detach(view);
					CacheManager.put(business.cache(), cacheKey, view);
				}
			}
			if (null == view) {
				throw new ExceptionEntityNotExist(flag, View.class);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			Wo wo = Wo.copier.copy(view);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends View {

		private static final long serialVersionUID = 3454132769791427909L;
		static WrapCopier<View, Wo> copier = WrapCopierFactory.wo(View.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}

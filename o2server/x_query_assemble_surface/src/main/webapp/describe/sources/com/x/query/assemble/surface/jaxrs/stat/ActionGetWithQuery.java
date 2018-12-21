package com.x.query.assemble.surface.jaxrs.stat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;

import net.sf.ehcache.Element;

class ActionGetWithQuery extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String queryFlag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Query query = business.pick(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag, Query.class);
			}
			if (!business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag, queryFlag);
			Element element = business.cache().get(cacheKey);
			Stat stat = null;
			if ((null != element) && (null != element.getObjectValue())) {
				stat = (Stat) element.getObjectValue();
			} else {
				String id = business.stat().getWithQuery(flag, query);
				stat = business.pick(id, Stat.class);
				if (null != stat) {
					business.entityManagerContainer().get(Stat.class).detach(stat);
					business.cache().put(new Element(cacheKey, stat));
				}
			}
			if (null == stat) {
				throw new ExceptionEntityNotExist(flag, Stat.class);
			}
			if (!business.readable(effectivePerson, stat)) {
				throw new ExceptionAccessDenied(effectivePerson, stat);
			}
			Wo wo = Wo.copier.copy(stat);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Stat {

		private static final long serialVersionUID = 3454132769791427909L;
		static WrapCopier<Stat, Wo> copier = WrapCopierFactory.wo(Stat.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
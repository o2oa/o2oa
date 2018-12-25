package com.x.query.assemble.surface.jaxrs.reveal;

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
import com.x.query.core.entity.Reveal;

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
			if (business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag, queryFlag);
			Element element = business.cache().get(cacheKey);
			Reveal reveal = null;
			if ((null != element) && (null != element.getObjectValue())) {
				reveal = (Reveal) element.getObjectValue();
			} else {
				String id = business.reveal().getWithQuery(flag, query);
				reveal = business.pick(id, Reveal.class);
				if (null != reveal) {
					business.entityManagerContainer().get(Reveal.class).detach(reveal);
					business.cache().put(new Element(cacheKey, reveal));
				}
			}
			if (null == reveal) {
				throw new ExceptionEntityNotExist(flag, Reveal.class);
			}
			if (business.readable(effectivePerson, reveal)) {
				throw new ExceptionAccessDenied(effectivePerson, reveal);
			}
			Wo wo = Wo.copier.copy(reveal);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Reveal {

		private static final long serialVersionUID = 3454132769791427909L;
		static WrapCopier<Reveal, Wo> copier = WrapCopierFactory.wo(Reveal.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
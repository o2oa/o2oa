package com.x.query.assemble.surface.jaxrs.reveal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Reveal reveal = business.pick(id, Reveal.class);
			if (null == reveal) {
				throw new ExceptionEntityNotExist(id, Reveal.class);
			}
			Query query = business.pick(reveal.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(reveal.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			if (!business.readable(effectivePerson, reveal)) {
				throw new ExceptionAccessDenied(effectivePerson, reveal);
			}
			Wo wo = Wo.copier.copy(reveal);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Reveal {

		private static final long serialVersionUID = -8067704098385000667L;

		/** 不输出data数据,单独处理 */
		static WrapCopier<Reveal, Wo> copier = WrapCopierFactory.wo(Reveal.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
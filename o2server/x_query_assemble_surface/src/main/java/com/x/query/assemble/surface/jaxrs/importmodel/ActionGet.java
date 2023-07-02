package com.x.query.assemble.surface.jaxrs.importmodel;

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
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			ImportModel model = business.pick(id, ImportModel.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(id, ImportModel.class);
			}
			Query query = business.pick(model.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(model.getQuery(), Query.class);
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

		private static final long serialVersionUID = -2420181761746203761L;

		/** 不输出data数据,单独处理 */
		static WrapCopier<ImportModel, Wo> copier = WrapCopierFactory.wo(ImportModel.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}

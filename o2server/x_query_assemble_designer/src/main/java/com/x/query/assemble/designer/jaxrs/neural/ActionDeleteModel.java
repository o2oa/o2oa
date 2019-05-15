package com.x.query.assemble.designer.jaxrs.neural;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Model;

class ActionDeleteModel extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String modelFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Model model = emc.flag(modelFlag, Model.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(modelFlag, Model.class);
			}
			if (StringUtils.equals(Model.STATUS_GENERATING, model.getStatus())) {
				throw new ExceptionGenerating(model.getName());
			}
			if (StringUtils.equals(Model.STATUS_LEARNING, model.getStatus())) {
				throw new ExceptionLearning(model.getName());
			}
			this.cleanOutValue(business, model);
			this.cleanInValue(business, model);
			this.cleanInText(business, model);
			this.cleanOutText(business, model);
			this.cleanEntry(business, model);
			emc.beginTransaction(Model.class);
			emc.remove(model, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Model.class);
			Wo wo = new Wo();
			wo.setId(model.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Model {

		private static final long serialVersionUID = -6541538280679110474L;

		static WrapCopier<Model, Wo> copier = WrapCopierFactory.wo(Model.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
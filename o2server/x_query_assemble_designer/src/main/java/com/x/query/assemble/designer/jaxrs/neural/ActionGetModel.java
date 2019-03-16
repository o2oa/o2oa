package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Model;

class ActionGetModel extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String modelFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Model model = emc.flag(modelFlag, Model.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(modelFlag, Model.class);
			}
			Wo wo = Wo.copier.copy(model);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Model {

		private static final long serialVersionUID = -6541538280679110474L;

		static WrapCopier<Model, Wo> copier = WrapCopierFactory.wo(Model.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Model.nnet_FIELDNAME, Model.intermediateNnet_FIELDNAME));

	}

}
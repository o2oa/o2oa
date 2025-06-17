package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.core.entity.AiModel;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionGetModel extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			AiModel aiModel = emc.find(id, AiModel.class);
			if(aiModel == null){
				throw new ExceptionEntityNotExist(id, AiModel.class);
			}
			Wo wo = Wo.copier.copy(aiModel);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends AiModel {
		static WrapCopier<AiModel, Wo> copier = WrapCopierFactory.wo(AiModel.class, Wo.class,
				null, JpaObject.FieldsInvisible);
	}

}

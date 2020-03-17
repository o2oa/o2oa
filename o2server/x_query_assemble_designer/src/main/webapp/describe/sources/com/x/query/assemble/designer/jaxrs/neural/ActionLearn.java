package com.x.query.assemble.designer.jaxrs.neural;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.neural.Entry;
import com.x.query.core.entity.neural.Model;

class ActionLearn extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String modelFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			Model model = emc.flag(modelFlag, Model.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(modelFlag, Model.class);
			}
			if (emc.countEqual(Entry.class, Entry.model_FIELDNAME, model.getId()) == 0) {
				throw new ExceptionEntryEmpty(model.getName());
			}
			if (StringUtils.equals(Model.STATUS_GENERATING, model.getStatus())) {
				throw new ExceptionGenerating(model.getName());
			}
			if (StringUtils.equals(Model.STATUS_LEARNING, model.getStatus())) {
				throw new ExceptionLearning(model.getName());
			}
			ThisApplication.context().applications().getQuery(x_query_service_processing.class,
					Applications.joinQueryUri("neural", "learn", "model", model.getId()));
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
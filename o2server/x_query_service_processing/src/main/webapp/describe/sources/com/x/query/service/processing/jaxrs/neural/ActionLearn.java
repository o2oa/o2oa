package com.x.query.service.processing.jaxrs.neural;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.neural.Model;

class ActionLearn extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLearn.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String modelFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Model model = emc.flag(modelFlag, Model.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(modelFlag, Model.class);
			}
			if (StringUtils.isNotEmpty(Learn.learningModel())) {
				throw new ExceptionLearn(model.getName());
			}
			new Thread() {
				public void run() {
					Learn learn;
					try {
						learn = Learn.newInstance();
						learn.execute(model.getId());
					} catch (Exception e) {
						logger.error(e);
					}
				};
			}.start();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
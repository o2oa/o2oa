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
import com.x.query.core.entity.neural.Project;

class ActionLearn extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLearn.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Project project = emc.flag(projectFlag, Project.class);
			if (null == project) {
				throw new ExceptionEntityNotExist(projectFlag, Project.class);
			}
			if (StringUtils.isNotEmpty(Learn.learningProject())) {
				throw new ExceptionLearn(project.getName());
			}
			new Thread() {
				public void run() {
					Learn learn;
					try {
						learn = Learn.newInstance();
						learn.execute(project.getId());
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
package com.x.query.assemble.surface.jaxrs.neural;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_query_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Work;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.neural.Project;

class ActionCalculate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCalculate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectFlag, String workId) throws Exception {
		logger.debug(effectivePerson, "projectFlag:{}, workId:{}.", projectFlag, workId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Project project = emc.flag(projectFlag, Project.class);
			if (null == project) {
				throw new ExceptionEntityNotExist(projectFlag, Project.class);
			}
			Work work = emc.flag(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Wo wo = ThisApplication
					.context().applications().getQuery(x_query_service_processing.class, Applications
							.joinQueryUri("neural", "calculate", "project", project.getId(), "work", work.getId()))
					.getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapStringList {

	}

}

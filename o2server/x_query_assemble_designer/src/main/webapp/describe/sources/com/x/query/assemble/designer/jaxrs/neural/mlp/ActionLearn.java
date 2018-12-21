package com.x.query.assemble.designer.jaxrs.neural.mlp;

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
import com.x.query.core.entity.neural.mlp.Entry;
import com.x.query.core.entity.neural.mlp.Project;

class ActionLearn extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			Project project = emc.flag(projectFlag, Project.class);
			if (null == project) {
				throw new ExceptionEntityNotExist(projectFlag, Project.class);
			}
			if (emc.countEqual(Entry.class, Entry.project_FIELDNAME, project.getId()) == 0) {
				throw new ExceptionEntryEmpty(project.getName());
			}
			if (StringUtils.equals(Project.STATUS_GENERATING, project.getStatus())) {
				throw new ExceptionGenerating(project.getName());
			}
			if (StringUtils.equals(Project.STATUS_LEARNING, project.getStatus())) {
				throw new ExceptionLearning(project.getName());
			}
			ThisApplication.context().applications().getQuery(x_query_service_processing.class,
					Applications.joinQueryUri("neural", "mlp", "learn", "project", project.getId()));
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
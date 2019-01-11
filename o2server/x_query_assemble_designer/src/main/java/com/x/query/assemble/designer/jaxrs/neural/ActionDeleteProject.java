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
import com.x.query.core.entity.neural.Project;

class ActionDeleteProject extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Project project = emc.flag(projectFlag, Project.class);
			if (null == project) {
				throw new ExceptionEntityNotExist(projectFlag, Project.class);
			}
			if (StringUtils.equals(Project.STATUS_GENERATING, project.getStatus())) {
				throw new ExceptionGenerating(project.getName());
			}
			if (StringUtils.equals(Project.STATUS_LEARNING, project.getStatus())) {
				throw new ExceptionLearning(project.getName());
			}
			this.cleanOutValue(business, project);
			this.cleanInValue(business, project);
			this.cleanEntry(business, project);
			emc.beginTransaction(Project.class);
			emc.remove(project, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Project.class);
			Wo wo = new Wo();
			wo.setId(project.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Project {

		private static final long serialVersionUID = -6541538280679110474L;

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo(Project.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
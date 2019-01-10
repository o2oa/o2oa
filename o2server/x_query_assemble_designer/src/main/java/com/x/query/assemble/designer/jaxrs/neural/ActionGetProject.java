package com.x.query.assemble.designer.jaxrs.neural;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Project;

class ActionGetProject extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Project project = emc.flag(projectFlag, Project.class);
			if (null == project) {
				throw new ExceptionEntityNotExist(projectFlag, Project.class);
			}
			Wo wo = Wo.copier.copy(project);
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
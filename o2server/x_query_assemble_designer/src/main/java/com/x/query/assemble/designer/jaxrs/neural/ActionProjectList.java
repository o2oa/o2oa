package com.x.query.assemble.designer.jaxrs.neural;

import java.util.List;

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

class ActionProjectList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Project> os = emc.listAll(Project.class);
			Wo wo = new Wo();
			result.setData(Wo.copier.copy(os));
			return result;
		}
	}

	public static class Wo extends Project {

		private static final long serialVersionUID = -6541538280679110474L;

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo(Project.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
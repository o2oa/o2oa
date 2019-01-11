package com.x.query.assemble.designer.jaxrs.neural;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Project;

class ActionCreateProject extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			emc.beginTransaction(Project.class);
			Project project = Wi.copier.copy(wi);
			// project.setStatus(Project.STATUS_NEW);
			emc.persist(project, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Project.class);
			Wo wo = new Wo();
			wo.setId(project.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Project {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Project> copier = WrapCopierFactory.wi(Wi.class, Project.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

	}

}
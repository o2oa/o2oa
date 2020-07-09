package com.x.program.center.jaxrs.market;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.InstallLog;

class ActionGetInstallLog extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			InstallLog installLog = emc.find(id, InstallLog.class);
			if(installLog==null){
				throw new ExceptionEntityNotExist(id, InstallLog.class);
			}
			Wo wo = Wo.copier.copy(installLog);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends InstallLog {

		private static final long serialVersionUID = 7332385892650739407L;

		static WrapCopier<InstallLog, Wo> copier = WrapCopierFactory.wo(InstallLog.class, Wo.class, null, Wo.FieldsInvisible);

	}
}
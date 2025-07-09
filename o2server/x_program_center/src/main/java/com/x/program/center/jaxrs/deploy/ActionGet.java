package com.x.program.center.jaxrs.deploy;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.DeployLog;

class ActionGet extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionGet.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug( "execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			DeployLog deployLog = emc.find(id, DeployLog.class);
			if (null == deployLog) {
				throw new ExceptionEntityNotExist(id);
			}
			Wo wo = Wo.copier.copy(deployLog);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends DeployLog {

		private static final long serialVersionUID = -1185168961925443180L;
		static WrapCopier<DeployLog, Wo> copier = WrapCopierFactory.wo(DeployLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}

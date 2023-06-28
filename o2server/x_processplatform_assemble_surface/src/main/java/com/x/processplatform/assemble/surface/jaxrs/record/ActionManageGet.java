package com.x.processplatform.assemble.surface.jaxrs.record;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Record rec = null;
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			rec = emc.find(id, Record.class);
			if (null == rec) {
				throw new ExceptionEntityNotExist(id, Record.class);
			}
			Application application = business.application().pick(rec.getApplication());
			Process process = business.process().pick(rec.getProcess());
			// 需要对这个应用的管理权限
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}
		Wo wo = Wo.copier.copy(rec);
		result.setData(wo);
		return result;
	}

	public static class Wo extends Record {

		private static final long serialVersionUID = -6482712324713975409L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
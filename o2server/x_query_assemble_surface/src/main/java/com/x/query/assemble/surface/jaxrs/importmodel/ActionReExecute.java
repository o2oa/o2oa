package com.x.query.assemble.surface.jaxrs.importmodel;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.ImportRecord;

class ActionReExecute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReExecute.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String recordId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ImportRecord record = business.pick(recordId, ImportRecord.class);
			if(record == null){
				throw new ExceptionEntityNotExist(recordId, ImportRecord.class);
			}
			ThisApplication.queueImportData.send(recordId);
			wo.setId(recordId);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {
	}
}

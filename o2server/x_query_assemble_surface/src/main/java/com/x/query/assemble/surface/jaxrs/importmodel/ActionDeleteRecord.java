package com.x.query.assemble.surface.jaxrs.importmodel;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.ImportRecord;
import com.x.query.core.entity.ImportRecordItem;

class ActionDeleteRecord extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDeleteRecord.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String recordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			ImportRecord record = emc.find(recordId, ImportRecord.class);
			if(record == null){
				throw new ExceptionEntityNotExist(recordId, ImportRecord.class);
			}
			if (!business.controllable(effectivePerson) && !effectivePerson.getDistinguishedName().equals(record.getCreatorPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<ImportRecordItem> itemList = emc.listEqual(ImportRecordItem.class, ImportRecordItem.recordId_FIELDNAME, record.getId());
			emc.beginTransaction(ImportRecord.class);
			emc.beginTransaction(ImportRecordItem.class);
			for (ImportRecordItem item : itemList) {
				emc.remove(item);
			}
			emc.remove(record);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}

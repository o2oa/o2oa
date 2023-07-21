package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.cms.core.entity.DocumentViewRecord;

/**
 * @author sword
 */
public class ActionQueryHasViewDocument extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(false);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Long count = emc.countEqualAndEqual(DocumentViewRecord.class, DocumentViewRecord.documentId_FIELDNAME, docId,
					DocumentViewRecord.viewerName_FIELDNAME, effectivePerson.getDistinguishedName());
			if(count!=null && count>0){
				wo.setValue(true);
			}
		}

		result.setData( wo );
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}

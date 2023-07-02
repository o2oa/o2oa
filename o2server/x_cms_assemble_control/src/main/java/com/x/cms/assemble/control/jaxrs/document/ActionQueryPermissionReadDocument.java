package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;

public class ActionQueryPermissionReadDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryPermissionReadDocument.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String queryPerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
		}
		Wo wo = new Wo();
		wo.setValue(false);
		result.setData(wo);
		Document document = documentQueryService.view( id, effectivePerson );
		if(document == null){
			return result;
		}
		wo.setValue(this.hasReadPermission(business, document, null, null, effectivePerson, queryPerson));


		return result;			
	}

	public static class Wo extends WrapBoolean {
		
	}

}
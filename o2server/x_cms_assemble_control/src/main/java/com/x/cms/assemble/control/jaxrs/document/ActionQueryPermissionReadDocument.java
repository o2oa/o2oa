package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import org.apache.commons.lang3.StringUtils;

public class ActionQueryPermissionReadDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryPermissionReadDocument.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String queryPerson) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(false);
		result.setData(wo);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.find(id, Document.class);
			if(document!=null){
				if(StringUtils.isNotBlank(queryPerson)){
					effectivePerson =  new EffectivePerson(queryPerson, TokenType.user,
							Config.token().getCipher(), Config.person().getEncryptType());
				}
				wo.setValue(business.isDocumentReader(effectivePerson, document));
			}
		}
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}

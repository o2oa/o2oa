package com.x.cms.assemble.control.jaxrs.correlation;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionListTypeCmsWithSiteWo;

class ActionListWithDocumentWithSite extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithDocumentWithSite.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String docId, String site) throws Exception {

		LOGGER.debug("execute:{}, docId:{}.", effectivePerson::getDistinguishedName, () -> docId);

		ActionResult<List<Wo>> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.find(docId, Document.class);
			if (null == document) {
				throw new ExceptionEntityNotExist(docId);
			}
			if (!business.isDocumentReader(effectivePerson, document)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}

		List<Wo> wos = ThisApplication.context().applications()
				.getQuery(
						effectivePerson.getDebugger(), x_correlation_service_processing.class, Applications
								.joinQueryUri("correlation", "list", "type", "cms", "document", docId, "site", site),
						docId)
				.getDataAsList(Wo.class);
		result.setData(wos);
		return result;
	}

	public static class Wo extends ActionListTypeCmsWithSiteWo {

		private static final long serialVersionUID = -7666329770246726197L;

	}

}

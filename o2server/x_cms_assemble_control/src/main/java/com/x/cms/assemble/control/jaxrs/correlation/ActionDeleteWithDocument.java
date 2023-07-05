package com.x.cms.assemble.control.jaxrs.correlation;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_correlation_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeCmsWo;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.ActionDeleteTypeProcessPlatformWi;

class ActionDeleteWithDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteWithDocument.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionDeleteTypeProcessPlatformWi req = new ActionDeleteTypeProcessPlatformWi();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.find(docId, Document.class);
			if (null == document) {
				throw new ExceptionEntityNotExist(docId);
			}
			if (!business.isDocumentEditor(effectivePerson, null, null, document)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			req.setIdList(wi.getIdList());
		}

		if (ListTools.isNotEmpty(wi.getIdList())) {
			Wo wo = ThisApplication.context().applications()
					.postQuery(effectivePerson.getDebugger(), x_correlation_service_processing.class,
							Applications.joinQueryUri("correlation", "delete", "type", "cms", "document", docId),
							req, docId)
					.getData(Wo.class);
			result.setData(wo);
		}
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6571915741994756275L;

		@FieldDescribe("取消关联目标.")
		private List<String> idList;

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

	}

	public static class Wo extends ActionDeleteTypeCmsWo {

		private static final long serialVersionUID = -6712098733513365965L;

	}

}

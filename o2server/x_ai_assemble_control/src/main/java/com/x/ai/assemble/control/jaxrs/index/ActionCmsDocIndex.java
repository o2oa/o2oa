package com.x.ai.assemble.control.jaxrs.index;

import com.x.ai.assemble.control.ThisApplication;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

class ActionCmsDocIndex extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionCmsDocIndex.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String docId) throws Exception {
		logger.info("{}操作添加索引文档:{}",effectivePerson.getDistinguishedName(), docId);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find(docId, Document.class);
			if (null == document) {
				throw new ExceptionEntityNotExist(docId);
			}
		}
		ThisApplication.queueDocumentIndex.send(docId);
		Wo wo = new Wo();
		wo.setId(docId);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

}

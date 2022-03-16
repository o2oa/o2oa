package com.x.processplatform.assemble.surface.jaxrs.sign;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class ActionDelete extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			DocSign docSign = emc.find(id, DocSign.class);
			if (null == docSign) {
				throw new ExceptionEntityNotExist(id, DocSign.class);
			}
			if(!business.canManageApplication(effectivePerson, null) &&
					!docSign.getPerson().equals(effectivePerson.getDistinguishedName())){
				throw new ExceptionAccessDenied(effectivePerson, id);
			}
			List<DocSignScrawl> signScrawlList = emc.listEqual(DocSignScrawl.class, DocSignScrawl.signId_FIELDNAME, docSign.getId());
			if(ListTools.isNotEmpty(signScrawlList)) {
				emc.beginTransaction(DocSignScrawl.class);
				for (DocSignScrawl signScrawl : signScrawlList) {
					if(StringUtils.isNotBlank(signScrawl.getStorage())) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class, signScrawl.getStorage());
						signScrawl.deleteContent(mapping);
					}
					emc.remove(signScrawl);
				}
			}
			emc.beginTransaction(DocSign.class);
			emc.remove(docSign);
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

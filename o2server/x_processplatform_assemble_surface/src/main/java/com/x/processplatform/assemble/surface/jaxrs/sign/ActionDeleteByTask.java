package com.x.processplatform.assemble.surface.jaxrs.sign;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
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

class ActionDeleteByTask extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteByTask.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String taskId) throws Exception {
		LOGGER.debug("execute:{}, taskId:{}.", effectivePerson::getDistinguishedName, () -> taskId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			wo.setValue(true);
			DocSign docSign = emc.firstEqual(DocSign.class, DocSign.taskId_FIELDNAME, taskId);
			if (null != docSign) {
				if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")
						&& !docSign.getPerson().equals(effectivePerson.getDistinguishedName()))) {
					throw new ExceptionAccessDenied(effectivePerson, taskId);
				}
				List<DocSignScrawl> signScrawlList = emc.listEqual(DocSignScrawl.class, DocSignScrawl.signId_FIELDNAME,
						docSign.getId());
				if (ListTools.isNotEmpty(signScrawlList)) {
					emc.beginTransaction(DocSignScrawl.class);
					for (DocSignScrawl signScrawl : signScrawlList) {
						if (StringUtils.isNotBlank(signScrawl.getStorage())) {
							StorageMapping mapping = ThisApplication.context().storageMappings()
									.get(DocSignScrawl.class, signScrawl.getStorage());
							signScrawl.deleteContent(mapping);
						}
						emc.remove(signScrawl);
					}
				}
				emc.beginTransaction(DocSign.class);
				emc.remove(docSign);
				emc.commit();
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -1716806175890811408L;

	}

}

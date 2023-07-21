package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
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
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionManageBatchDeleteWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageBatchDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageBatchDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			LOGGER.print("execute:{},.", effectivePerson::getDistinguishedName);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if (ListTools.isNotEmpty(wi.getIdList())) {
				for (String id : wi.getIdList()) {
					Attachment attachment = emc.find(id.trim(), Attachment.class);
					if (attachment != null) {
						LOGGER.print("manageBatchDelete attachment:{}—{}", attachment.getId(), attachment.getName());
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								attachment.getStorage());
						attachment.deleteContent(mapping);
						emc.beginTransaction(Attachment.class);
						emc.remove(attachment);
						emc.commit();
					}
				}
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionManageBatchDelete$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -6186591656948482426L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionManageBatchDelete$Wi")
	public static class Wi extends ActionManageBatchDeleteWi {

		private static final long serialVersionUID = 3267682362954096818L;

	}

}

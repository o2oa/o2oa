package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.io.FilenameUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DocumentTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;

import net.sf.ehcache.Element;

class ActionPreviewPdf extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPreviewPdf.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}

			if (!business.readableWithJob(effectivePerson, attachment.getJob())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			byte[] bytes = DocumentTools.toPdf(attachment.getName(), attachment.getBytes(), "");

			PreviewPdfResultObject obj = new PreviewPdfResultObject();
			obj.setPerson(effectivePerson.getDistinguishedName());
			obj.setBytes(bytes);
			obj.setName(FilenameUtils.getBaseName(attachment.getName()) + ".pdf");

			String cacheKey = StringTools.uniqueToken();
			cachePreviewPdf.put(new Element(cacheKey, obj));
			Wo wo = new Wo();
			wo.setId(cacheKey);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WoWorkControl extends WorkControl {
	}

	public static class WoWorkCompletedControl extends WorkCompletedControl {
	}

}
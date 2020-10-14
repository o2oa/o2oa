package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform.WorkCompletedExtensionEvent;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;

/**
 * 管理员下载
 * 
 * @author zhour
 *
 */
class ActionManageDownload extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			if (BooleanUtils.isNotTrue(business.canManageApplicationOrProcess(effectivePerson,
					attachment.getApplication(), attachment.getProcess()))) {
				throw new ExceptionAccessDenied(effectivePerson, attachment);
			}
		}
		StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
				attachment.getStorage());
		String fileName = attachment.getName()
				+ (StringUtils.isNotEmpty(attachment.getExtension()) ? "." + attachment.getExtension() : "");
		byte[] bytes = attachment.readContent(mapping);
		Wo wo = new Wo(bytes, this.contentType(false, fileName), this.contentDisposition(false, fileName));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
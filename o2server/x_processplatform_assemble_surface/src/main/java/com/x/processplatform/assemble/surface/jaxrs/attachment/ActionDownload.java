package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDownload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDownload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String fileName) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Work work = null;
		WorkCompleted workCompleted = null;
		Attachment attachment = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			if (BooleanUtils.isNotTrue(new JobControlBuilder(effectivePerson, business, attachment.getJob())
					.enableAllowVisit().build().getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, id);
			}

			if ((!Config.processPlatform().getExtensionEvents().getWorkAttachmentDownloadEvents().isEmpty()) || (Config
					.processPlatform().getExtensionEvents().getWorkCompletedAttachmentDownloadEvents().isEmpty())) {
				List<Work> workList = business.work().listWithJobObject(attachment.getJob());
				if (ListTools.isEmpty(workList)) {
					List<WorkCompleted> list = business.workCompleted().listWithJobObject(attachment.getJob());
					if (ListTools.isNotEmpty(list)) {
						workCompleted = list.get(0);
					}
				} else {
					work = workList.get(0);
				}
			}
		}
		StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
				attachment.getStorage());
		if (StringUtils.isBlank(fileName)) {
			fileName = attachment.getName();
		} else {
			String extension = FilenameUtils.getExtension(fileName);
			if (StringUtils.isEmpty(extension)) {
				fileName = fileName + "." + attachment.getExtension();
			}
		}
		byte[] bytes = read(effectivePerson, mapping, work, workCompleted, attachment);
		Wo wo = new Wo(bytes, this.contentType(false, fileName), this.contentDisposition(false, fileName));
		result.setData(wo);
		return result;
	}



	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionDownload$Wo")
	public static class Wo extends WoFile {

		private static final long serialVersionUID = 8958654624399659293L;

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}


}

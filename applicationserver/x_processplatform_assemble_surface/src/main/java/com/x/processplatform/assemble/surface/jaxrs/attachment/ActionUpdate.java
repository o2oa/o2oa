package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionUpdate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, String fileName, byte[] bytes,
			FormDataContentDisposition disposition, String extraParam) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("receive id:{}, workId:{}, fileName:{}, extraParam:{}.", id, workId, fileName, extraParam);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 后面要重新保存 */
			Work work = emc.find(workId, Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			// if (!work.getAttachmentList().contains(id)) {
			// throw new ExceptionWorkNotContainsAttachment(work.getTitle(), work.getId(),
			// attachment.getName(),
			// attachment.getId());
			// }
			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			/** 禁止不带扩展名的文件上传 */
			// if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
			// throw new ExceptionEmptyExtension(fileName);
			// }
			/** 禁止不同的扩展名上传 */
			// if
			// (!Objects.equals(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)),
			// attachment.getExtension())) {
			// throw new ExceptionExtensionNotMatch(fileName, attachment.getExtension());
			// }
			/** 统计待办数量判断用户是否可以上传附件 */
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
			// if (business.attachment().multiReferenced(attachment)) {
			// throw new ExceptionMultiReferenced(attachment.getName(),
			// attachment.getId());
			// }
			/* 天印扩展 */
			if (StringUtils.isNotEmpty(extraParam)) {
				WiExtraParam wiExtraParam = gson.fromJson(extraParam, WiExtraParam.class);
				if (StringUtils.isNotEmpty(wiExtraParam.getFileName())) {
					fileName = wiExtraParam.getFileName();
				}
			}

			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			emc.beginTransaction(Attachment.class);
			attachment.updateContent(mapping, bytes, fileName);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkControl {
	}

	

}

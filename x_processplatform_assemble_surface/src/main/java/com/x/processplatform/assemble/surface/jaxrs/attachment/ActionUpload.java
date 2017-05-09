package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionUpload extends ActionBase {
	ActionResult<IdWo> execute(EffectivePerson effectivePerson, String workId, String site, byte[] bytes,
			FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<IdWo> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 后面要重新保存 */
			Work work = emc.find(workId, Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new WorkNotExistedException(workId);
			}
			/** 统计待办数量判断用户是否可以上传附件 */
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			if (BooleanUtils.isNotTrue(control.getAllowProcessing())) {
				throw new WorkAccessDeniedException(effectivePerson.getName(), work.getTitle(), work.getId());
			}
			String fileName = FilenameUtils.getName(new String(
					disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(fileName)) {
				throw new EmptyExtensionException(fileName);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(work, effectivePerson, site);
			attachment.saveContent(mapping, bytes, fileName);
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			work.getAttachmentList().add(attachment.getId());
			emc.commit();
			result.setData(new IdWo(attachment.getId()));
			return result;
		}
	}

	private Attachment concreteAttachment(Work work, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getName());
		attachment.setLastUpdatePerson(effectivePerson.getName());
		attachment.setSite(site);
		/** 用于判断目录的值 */
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		return attachment;
	}
}

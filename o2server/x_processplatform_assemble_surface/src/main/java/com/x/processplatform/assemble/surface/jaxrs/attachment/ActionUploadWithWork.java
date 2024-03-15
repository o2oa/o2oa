package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionUploadWithWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadWithWork.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String site, String fileName, byte[] bytes,
			FormDataContentDisposition disposition, String extraParam) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 后面要重新保存 */
			Work work = emc.find(workId, Work.class);
			/* 判断work是否存在 */
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, work);
			}

			/* 天谷印章扩展 */
			if (StringUtils.isNotEmpty(extraParam)) {
				WiExtraParam wiExtraParam = gson.fromJson(extraParam, WiExtraParam.class);
				if (StringUtils.isNotEmpty(wiExtraParam.getFileName())) {
					fileName = wiExtraParam.getFileName();
				}
				if (StringUtils.isNotEmpty(wiExtraParam.getSite())) {
					site = wiExtraParam.getSite();
				}
			}

			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			/* 调整可能的附件名称 */
			fileName = this.adjustFileName(business, work.getJob(), fileName);

			this.verifyConstraint(bytes.length, fileName, null);

			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(work, effectivePerson, site);
			attachment.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			LOGGER.debug("filename:{}, file type:{}.", attachment.getName(), attachment.getType());
			if (Config.query().getExtractImage() && ExtractTextTools.supportImage(attachment.getName())
					&& ExtractTextTools.available(bytes)) {
				attachment.setText(ExtractTextTools.image(bytes));
				LOGGER.debug("filename:{}, file type:{}, text:{}.", attachment.getName(), attachment.getType(),
						attachment.getText());
			}

			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	private Attachment concreteAttachment(Work work, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
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

	public static class Wo extends WoId {

		private static final long serialVersionUID = -5924606976588092494L;

	}

}
